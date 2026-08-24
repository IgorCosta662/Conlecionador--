package com.example.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// ---------------- Scryfall API Interface ----------------
interface ScryfallRetrofitService {
    @GET("cards/search")
    suspend fun searchCards(
        @Query("q") query: String,
        @Query("order") order: String = "name"
    ): ScryfallSearchResponse

    @GET("cards/named")
    suspend fun getCardNamed(
        @Query("fuzzy") name: String
    ): ScryfallCard

    @GET("cards/{id}")
    suspend fun getCardById(
        @Path("id") id: String
    ): ScryfallCard
}

// ---------------- TCGDex API Interface ----------------
interface TcgdexRetrofitService {
    @GET("cards")
    suspend fun searchCards(
        @Query("name") name: String
    ): List<TcgdexCardBrief>

    @GET("cards/{id}")
    suspend fun getCardDetail(
        @Path("id") id: String
    ): TcgdexCardDetail

    @GET("sets")
    suspend fun getSets(): List<TcgdexSetBrief>
}

// ---------------- Unified Online TCG Service ----------------
object TcgOnlineService {
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    // 1. Scryfall API Service (https://api.scryfall.com)
    val scryfall: ScryfallRetrofitService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.scryfall.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ScryfallRetrofitService::class.java)
    }

    // 2. TCGDex Portuguese API Service (https://api.tcgdex.net/v2/pt/)
    val tcgdexPt: TcgdexRetrofitService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.tcgdex.net/v2/pt/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(TcgdexRetrofitService::class.java)
    }

    // 3. TCGDex English API Service (https://api.tcgdex.net/v2/en/)
    val tcgdexEn: TcgdexRetrofitService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.tcgdex.net/v2/en/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(TcgdexRetrofitService::class.java)
    }

    /**
     * Search Pokémon cards online via TCGDex API (Portuguese first, with English fallback)
     */
    suspend fun searchPokemonCards(query: String): List<TcgdexCardBrief> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        try {
            val resultsPt = tcgdexPt.searchCards(query.trim())
            if (resultsPt.isNotEmpty()) {
                return@withContext resultsPt.take(30)
            }
        } catch (_: Exception) {}

        try {
            val resultsEn = tcgdexEn.searchCards(query.trim())
            return@withContext resultsEn.take(30)
        } catch (_: Exception) {
            emptyList()
        }
    }

    /**
     * Get Pokémon card full details by card ID via TCGDex
     */
    suspend fun getPokemonCardDetail(cardId: String): TcgdexCardDetail? = withContext(Dispatchers.IO) {
        try {
            return@withContext tcgdexPt.getCardDetail(cardId)
        } catch (_: Exception) {}

        try {
            return@withContext tcgdexEn.getCardDetail(cardId)
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Search Magic: The Gathering cards online via dedicated Scryfall API Data Service
     */
    suspend fun searchMagicCards(query: String): List<ScryfallCard> {
        return com.example.api.scryfall.ScryfallDataService.searchCardsByName(query)
    }

    /**
     * Get single Magic card by fuzzy name using Scryfall API
     */
    suspend fun getMagicCardByFuzzyName(query: String): ScryfallCard? {
        return com.example.api.scryfall.ScryfallDataService.findCardByFuzzyName(query)
    }

    /**
     * Get exact Magic card by name from Scryfall API
     */
    suspend fun getMagicCardByName(cardName: String): ScryfallCard? = withContext(Dispatchers.IO) {
        try {
            return@withContext scryfall.getCardNamed(cardName.trim())
        } catch (_: Exception) {
            null
        }
    }
}
