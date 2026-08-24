package com.example.api.scryfall

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

/**
 * Dedicated Retrofit API interface for Scryfall REST API
 * Documentation: https://scryfall.com/docs/api
 */
interface ScryfallApiService {

    /**
     * Search for Magic: The Gathering cards by full query syntax (e.g. name, colors, sets, types)
     * Example: "Black Lotus", "c:blue t:instant", "set:mh2 r:mythic"
     */
    @GET("cards/search")
    suspend fun searchCards(
        @Query("q") query: String,
        @Query("order") order: String = "name",
        @Query("dir") dir: String = "auto",
        @Query("page") page: Int = 1,
        @Query("include_extras") includeExtras: Boolean = false,
        @Query("include_multilingual") includeMultilingual: Boolean = false
    ): Response<ScryfallSearchResponse>

    /**
     * Finds a card using fuzzy matching on the name.
     */
    @GET("cards/named")
    suspend fun getCardNamedFuzzy(
        @Query("fuzzy") fuzzyName: String,
        @Query("set") setCode: String? = null
    ): Response<ScryfallCard>

    /**
     * Finds a card using exact name matching.
     */
    @GET("cards/named")
    suspend fun getCardNamedExact(
        @Query("exact") exactName: String,
        @Query("set") setCode: String? = null
    ): Response<ScryfallCard>

    /**
     * Returns a single card with the given Scryfall ID.
     */
    @GET("cards/{id}")
    suspend fun getCardById(
        @Path("id") id: String
    ): Response<ScryfallCard>

    /**
     * Returns a random card object.
     */
    @GET("cards/random")
    suspend fun getRandomCard(
        @Query("q") query: String? = null
    ): Response<ScryfallCard>

    /**
     * Autocomplete card names based on user query string.
     */
    @GET("cards/autocomplete")
    suspend fun autocompleteCardName(
        @Query("q") query: String
    ): Response<ScryfallAutocompleteResponse>

    /**
     * Returns a list of all Magic: The Gathering sets from Scryfall.
     */
    @GET("sets")
    suspend fun getSets(): Response<ScryfallSetListResponse>

    /**
     * Returns a single Magic set by its code.
     */
    @GET("sets/{code}")
    suspend fun getSetByCode(
        @Path("code") code: String
    ): Response<ScryfallSet>
}

/**
 * Dedicated Scryfall Data Service and Repository
 * Provides clean coroutine-based functions with error handling, caching, and fallback logic.
 */
object ScryfallDataService {

    private const val BASE_URL = "https://api.scryfall.com/"

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "CollectorHub/2.0 (Android; ScryfallMTG)")
                .header("Accept", "application/json;q=0.9,*/*;q=0.8")
                .build()
            chain.proceed(request)
        }
        .build()

    val api: ScryfallApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ScryfallApiService::class.java)
    }

    // In-memory caches for fast repeated queries
    private val searchCache = mutableMapOf<String, List<ScryfallCard>>()
    private val cardDetailsCache = mutableMapOf<String, ScryfallCard>()
    private val setCardsCache = mutableMapOf<String, List<ScryfallCard>>()
    private var cachedSets: List<ScryfallSet>? = null

    /**
     * Searches cards by name or syntax query.
     * Returns an empty list gracefully on failure or no matches.
     */
    suspend fun searchCardsByName(query: String, maxResults: Int = 30): List<ScryfallCard> =
        withContext(Dispatchers.IO) {
            val cleanQuery = query.trim()
            if (cleanQuery.isBlank()) return@withContext emptyList()

            // Check cache
            val cacheKey = cleanQuery.lowercase()
            searchCache[cacheKey]?.let { return@withContext it.take(maxResults) }

            try {
                val response = api.searchCards(query = cleanQuery)
                if (response.isSuccessful && response.body() != null) {
                    val list = response.body()!!.data
                    searchCache[cacheKey] = list
                    list.forEach { card ->
                        if (card.id.isNotBlank()) {
                            cardDetailsCache[card.id] = card
                        }
                    }
                    return@withContext list.take(maxResults)
                }
            } catch (_: Exception) {
                // Return empty list on network or parse error
            }
            emptyList()
        }

    /**
     * Finds a card with fuzzy name resolution (e.g. "black lotu" -> Black Lotus)
     */
    suspend fun findCardByFuzzyName(fuzzyName: String, setCode: String? = null): ScryfallCard? =
        withContext(Dispatchers.IO) {
            val q = fuzzyName.trim()
            if (q.isBlank()) return@withContext null

            try {
                val response = api.getCardNamedFuzzy(fuzzyName = q, setCode = setCode)
                if (response.isSuccessful) {
                    val card = response.body()
                    if (card != null) {
                        cardDetailsCache[card.id] = card
                        return@withContext card
                    }
                }
            } catch (_: Exception) {
                // Fallback to searching
            }

            // Fallback: regular search
            val results = searchCardsByName(q, maxResults = 1)
            results.firstOrNull()
        }

    /**
     * Real-time live suggestions combining Scryfall Autocomplete with Fuzzy Matching.
     * Returns name recommendations and direct card resolution concurrently.
     */
    suspend fun getLiveSuggestionsAndFuzzyCard(query: String): ScryfallLiveSuggestions =
        withContext(Dispatchers.IO) {
            val clean = query.trim()
            if (clean.length < 2) {
                return@withContext ScryfallLiveSuggestions(query = clean, suggestions = emptyList(), directFuzzyCard = null)
            }

            var suggestions = emptyList<String>()
            var fuzzyCard: ScryfallCard? = null

            try {
                kotlinx.coroutines.coroutineScope {
                    val autocompleteJob = async {
                        autocompleteCardNames(clean)
                    }
                    val fuzzyJob = async {
                        findCardByFuzzyName(clean)
                    }

                    suggestions = autocompleteJob.await()
                    fuzzyCard = fuzzyJob.await()
                }
            } catch (_: Exception) {
                // Fallback gracefully
            }

            ScryfallLiveSuggestions(
                query = clean,
                suggestions = suggestions,
                directFuzzyCard = fuzzyCard,
                isSearching = false
            )
        }

    /**
     * Fetches all Magic: The Gathering sets from Scryfall API, with release years and categorization.
     */
    suspend fun getSets(): List<ScryfallSet> = withContext(Dispatchers.IO) {
        cachedSets?.let { return@withContext it }

        try {
            val response = api.getSets()
            if (response.isSuccessful && response.body() != null) {
                val sets = response.body()!!.data
                    .filter { !it.digital && it.cardCount > 0 }
                    .sortedByDescending { it.releasedAt ?: "0000" }
                if (sets.isNotEmpty()) {
                    cachedSets = sets
                    return@withContext sets
                }
            }
        } catch (_: Exception) {
            // Fallback to built-in curated popular MTG sets
        }

        // Curated fallback sets if offline
        val fallback = getFallbackSets()
        cachedSets = fallback
        fallback
    }

    /**
     * Fetches cards for a specific Magic: The Gathering set.
     */
    suspend fun getCardsForSet(setCode: String, maxCards: Int = 100): List<ScryfallCard> =
        withContext(Dispatchers.IO) {
            val cleanCode = setCode.trim().lowercase()
            if (cleanCode.isBlank()) return@withContext emptyList()

            setCardsCache[cleanCode]?.let { return@withContext it.take(maxCards) }

            try {
                val response = api.searchCards(
                    query = "set:$cleanCode",
                    order = "set",
                    includeExtras = false
                )
                if (response.isSuccessful && response.body() != null) {
                    val list = response.body()!!.data
                    setCardsCache[cleanCode] = list
                    list.forEach { card ->
                        if (card.id.isNotBlank()) {
                            cardDetailsCache[card.id] = card
                        }
                    }
                    return@withContext list.take(maxCards)
                }
            } catch (_: Exception) {
                // Fallback attempt with exact code
            }

            emptyList()
        }

    /**
     * Fetches card by its exact Scryfall UUID.
     */
    suspend fun getCardById(id: String): ScryfallCard? = withContext(Dispatchers.IO) {
        if (id.isBlank()) return@withContext null
        cardDetailsCache[id]?.let { return@withContext it }

        try {
            val response = api.getCardById(id)
            if (response.isSuccessful) {
                val card = response.body()
                if (card != null) {
                    cardDetailsCache[card.id] = card
                }
                return@withContext card
            }
        } catch (_: Exception) {
            // graceful null
        }
        null
    }

    /**
     * Gets a random Magic card (optionally filtered, e.g. "is:commander")
     */
    suspend fun getRandomCard(queryFilter: String? = null): ScryfallCard? = withContext(Dispatchers.IO) {
        try {
            val response = api.getRandomCard(query = queryFilter)
            if (response.isSuccessful) {
                return@withContext response.body()
            }
        } catch (_: Exception) {
            // graceful null
        }
        null
    }

    /**
     * Gets autocomplete suggestions for card names
     */
    suspend fun autocompleteCardNames(prefix: String): List<String> = withContext(Dispatchers.IO) {
        if (prefix.length < 2) return@withContext emptyList()
        try {
            val response = api.autocompleteCardName(prefix.trim())
            if (response.isSuccessful && response.body() != null) {
                return@withContext response.body()!!.data
            }
        } catch (_: Exception) {
            // graceful fallback
        }
        emptyList()
    }

    /**
     * Curated list of iconic and recent MTG sets for offline or instant display
     */
    private fun getFallbackSets(): List<ScryfallSet> = listOf(
        ScryfallSet(id = "dsk", code = "dsk", name = "Duskmourn: House of Horror", releasedAt = "2024-09-27", setType = "expansion", cardCount = 387),
        ScryfallSet(id = "blb", code = "blb", name = "Bloomburrow", releasedAt = "2024-08-02", setType = "expansion", cardCount = 281),
        ScryfallSet(id = "mh3", code = "mh3", name = "Modern Horizons 3", releasedAt = "2024-06-14", setType = "draft_innovation", cardCount = 387),
        ScryfallSet(id = "otj", code = "otj", name = "Outlaws of Thunder Junction", releasedAt = "2024-04-19", setType = "expansion", cardCount = 276),
        ScryfallSet(id = "pip", code = "pip", name = "Fallout Commander", releasedAt = "2024-03-08", setType = "commander", cardCount = 348),
        ScryfallSet(id = "mkm", code = "mkm", name = "Murders at Karlov Manor", releasedAt = "2024-02-09", setType = "expansion", cardCount = 286),
        ScryfallSet(id = "rvr", code = "rvr", name = "Ravnica Remastered", releasedAt = "2024-01-12", setType = "masters", cardCount = 292),
        ScryfallSet(id = "lci", code = "lci", name = "The Lost Caverns of Ixalan", releasedAt = "2023-11-17", setType = "expansion", cardCount = 291),
        ScryfallSet(id = "who", code = "who", name = "Doctor Who Commander", releasedAt = "2023-10-13", setType = "commander", cardCount = 590),
        ScryfallSet(id = "woe", code = "woe", name = "Wilds of Eldraine", releasedAt = "2023-09-08", setType = "expansion", cardCount = 276),
        ScryfallSet(id = "cmm", code = "cmm", name = "Commander Masters", releasedAt = "2023-08-04", setType = "masters", cardCount = 436),
        ScryfallSet(id = "ltr", code = "ltr", name = "The Lord of the Rings: Tales of Middle-earth", releasedAt = "2023-06-23", setType = "draft_innovation", cardCount = 281),
        ScryfallSet(id = "mom", code = "mom", name = "March of the Machine", releasedAt = "2023-04-21", setType = "expansion", cardCount = 281),
        ScryfallSet(id = "one", code = "one", name = "Phyrexia: All Will Be One", releasedAt = "2023-02-10", setType = "expansion", cardCount = 271),
        ScryfallSet(id = "dmr", code = "dmr", name = "Dominaria Remastered", releasedAt = "2023-01-13", setType = "masters", cardCount = 271),
        ScryfallSet(id = "bro", code = "bro", name = "The Brothers' War", releasedAt = "2022-11-18", setType = "expansion", cardCount = 287),
        ScryfallSet(id = "dmu", code = "dmu", name = "Dominaria United", releasedAt = "2022-09-09", setType = "expansion", cardCount = 281),
        ScryfallSet(id = "2x2", code = "2x2", name = "Double Masters 2022", releasedAt = "2022-07-08", setType = "masters", cardCount = 332),
        ScryfallSet(id = "clb", code = "clb", name = "Commander Legends: Battle for Baldur's Gate", releasedAt = "2022-06-10", setType = "commander", cardCount = 361),
        ScryfallSet(id = "snc", code = "snc", name = "Streets of New Capenna", releasedAt = "2022-04-29", setType = "expansion", cardCount = 281),
        ScryfallSet(id = "neo", code = "neo", name = "Kamigawa: Neon Dynasty", releasedAt = "2022-02-18", setType = "expansion", cardCount = 302),
        ScryfallSet(id = "mh2", code = "mh2", name = "Modern Horizons 2", releasedAt = "2021-06-18", setType = "draft_innovation", cardCount = 303),
        ScryfallSet(id = "cmr", code = "cmr", name = "Commander Legends", releasedAt = "2020-11-20", setType = "commander", cardCount = 361),
        ScryfallSet(id = "znr", code = "znr", name = "Zendikar Rising", releasedAt = "2020-09-25", setType = "expansion", cardCount = 280),
        ScryfallSet(id = "eld", code = "eld", name = "Throne of Eldraine", releasedAt = "2019-10-04", setType = "expansion", cardCount = 269),
        ScryfallSet(id = "war", code = "war", name = "War of the Spark", releasedAt = "2019-05-03", setType = "expansion", cardCount = 264),
        ScryfallSet(id = "lea", code = "lea", name = "Limited Edition Alpha", releasedAt = "1993-08-05", setType = "core", cardCount = 295)
    )
}
