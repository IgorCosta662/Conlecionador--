package com.example.api.scryfall

import com.example.api.PriceSourceRegistry
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Scryfall API Search Response
 * Reference: https://scryfall.com/docs/api/cards/search
 */
@JsonClass(generateAdapter = true)
data class ScryfallSearchResponse(
    @Json(name = "total_cards") val totalCards: Int = 0,
    @Json(name = "has_more") val hasMore: Boolean = false,
    @Json(name = "next_page") val nextPage: String? = null,
    @Json(name = "data") val data: List<ScryfallCard> = emptyList()
)

/**
 * Scryfall Autocomplete Response
 * Reference: https://scryfall.com/docs/api/cards/autocomplete
 */
@JsonClass(generateAdapter = true)
data class ScryfallAutocompleteResponse(
    @Json(name = "total_values") val totalValues: Int = 0,
    @Json(name = "data") val data: List<String> = emptyList()
)

/**
 * Scryfall Set Response List
 * Reference: https://scryfall.com/docs/api/sets
 */
@JsonClass(generateAdapter = true)
data class ScryfallSetListResponse(
    @Json(name = "has_more") val hasMore: Boolean = false,
    @Json(name = "data") val data: List<ScryfallSet> = emptyList()
)

/**
 * Scryfall Set Model with release year and metadata
 */
@JsonClass(generateAdapter = true)
data class ScryfallSet(
    @Json(name = "id") val id: String = "",
    @Json(name = "code") val code: String = "",
    @Json(name = "name") val name: String = "",
    @Json(name = "uri") val uri: String? = null,
    @Json(name = "scryfall_uri") val scryfallUri: String? = null,
    @Json(name = "search_uri") val searchUri: String? = null,
    @Json(name = "released_at") val releasedAt: String? = null,
    @Json(name = "set_type") val setType: String? = null,
    @Json(name = "card_count") val cardCount: Int = 0,
    @Json(name = "digital") val digital: Boolean = false,
    @Json(name = "parent_set_code") val parentSetCode: String? = null,
    @Json(name = "icon_svg_uri") val iconSvgUri: String? = null
) {
    val releaseYear: String
        get() = releasedAt?.take(4)?.takeIf { it.isNotBlank() } ?: "N/A"

    val formattedSetType: String
        get() = when (setType?.lowercase()) {
            "core" -> "Edição Principal (Core)"
            "expansion" -> "Expansão Regular"
            "masters" -> "Masters / Reimpressões"
            "commander" -> "Commander"
            "draft_innovation" -> "Draft Inovador / Horizons"
            "box", "memorabilia", "arsenal" -> "Especial / Box"
            "funny" -> "Un-Set / Especial"
            "promo", "token" -> "Promocional"
            else -> setType?.replaceFirstChar { it.uppercase() } ?: "Expansão"
        }
}

/**
 * Result structure combining live Scryfall name suggestions and fuzzy card match
 */
data class ScryfallLiveSuggestions(
    val query: String,
    val suggestions: List<String> = emptyList(),
    val directFuzzyCard: ScryfallCard? = null,
    val isSearching: Boolean = false
)

/**
 * Detailed Scryfall Card Model with complete MTG metadata
 */
@JsonClass(generateAdapter = true)
data class ScryfallCard(
    @Json(name = "id") val id: String = "",
    @Json(name = "oracle_id") val oracleId: String? = null,
    @Json(name = "name") val name: String = "",
    @Json(name = "lang") val lang: String = "en",
    @Json(name = "released_at") val releasedAt: String? = null,
    @Json(name = "uri") val uri: String? = null,
    @Json(name = "scryfall_uri") val scryfallUri: String? = null,
    @Json(name = "layout") val layout: String? = null,
    @Json(name = "highres_image") val highresImage: Boolean = true,
    @Json(name = "image_status") val imageStatus: String? = null,
    @Json(name = "image_uris") val imageUris: ScryfallImageUris? = null,
    @Json(name = "mana_cost") val manaCost: String? = null,
    @Json(name = "cmc") val cmc: Double? = null,
    @Json(name = "type_line") val typeLine: String? = null,
    @Json(name = "oracle_text") val oracleText: String? = null,
    @Json(name = "printed_name") val printedName: String? = null,
    @Json(name = "printed_type_line") val printedTypeLine: String? = null,
    @Json(name = "printed_text") val printedText: String? = null,
    @Json(name = "power") val power: String? = null,
    @Json(name = "toughness") val toughness: String? = null,
    @Json(name = "loyalty") val loyalty: String? = null,
    @Json(name = "colors") val colors: List<String>? = null,
    @Json(name = "color_identity") val colorIdentity: List<String>? = null,
    @Json(name = "keywords") val keywords: List<String>? = null,
    @Json(name = "legalities") val legalities: Map<String, String>? = null,
    @Json(name = "games") val games: List<String>? = null,
    @Json(name = "reserved") val reserved: Boolean = false,
    @Json(name = "foil") val foil: Boolean = false,
    @Json(name = "nonfoil") val nonfoil: Boolean = true,
    @Json(name = "finishes") val finishes: List<String>? = null,
    @Json(name = "set") val set: String = "",
    @Json(name = "set_name") val setName: String = "",
    @Json(name = "set_type") val setType: String? = null,
    @Json(name = "set_uri") val setUri: String? = null,
    @Json(name = "collector_number") val collectorNumber: String = "",
    @Json(name = "digital") val digital: Boolean = false,
    @Json(name = "rarity") val rarity: String = "common",
    @Json(name = "flavor_text") val flavorText: String? = null,
    @Json(name = "artist") val artist: String? = null,
    @Json(name = "border_color") val borderColor: String? = null,
    @Json(name = "frame") val frame: String? = null,
    @Json(name = "prices") val prices: ScryfallPrices? = null,
    @Json(name = "card_faces") val cardFaces: List<ScryfallCardFace>? = null
) {
    /**
     * Resolves the highest quality image available for the card.
     * Supports single-faced and double-faced / transform / split cards.
     */
    fun getHighResImage(): String? {
        imageUris?.large?.let { return it }
        imageUris?.normal?.let { return it }
        imageUris?.png?.let { return it }
        imageUris?.small?.let { return it }
        cardFaces?.firstOrNull()?.imageUris?.large?.let { return it }
        cardFaces?.firstOrNull()?.imageUris?.normal?.let { return it }
        cardFaces?.firstOrNull()?.imageUris?.png?.let { return it }
        return null
    }

    /**
     * Resolves the art crop image for headers or avatars.
     */
    fun getArtCropImage(): String? {
        imageUris?.artCrop?.let { return it }
        cardFaces?.firstOrNull()?.imageUris?.artCrop?.let { return it }
        return getHighResImage()
    }

    /**
     * Estimates market price in Brazilian Real (BRL) with real USD/EUR exchange rates.
     */
    fun getEstimatedPriceBrl(): Double {
        val usd = prices?.usd?.toDoubleOrNull() ?: prices?.usdFoil?.toDoubleOrNull() ?: prices?.usdEtched?.toDoubleOrNull()
        if (usd != null) {
            return Math.round(usd * PriceSourceRegistry.USD_BRL_EXCHANGE_RATE * 100.0) / 100.0
        }
        val eur = prices?.eur?.toDoubleOrNull() ?: prices?.eurFoil?.toDoubleOrNull()
        if (eur != null) {
            return Math.round(eur * PriceSourceRegistry.EUR_BRL_EXCHANGE_RATE * 100.0) / 100.0
        }
        return when (rarity.lowercase()) {
            "mythic", "special" -> 85.0
            "rare" -> 25.0
            "uncommon" -> 5.0
            else -> 1.50
        }
    }

    /**
     * Display label for rarities in Portuguese
     */
    val rarityPt: String
        get() = when (rarity.lowercase()) {
            "mythic" -> "Mítica Rara"
            "special" -> "Mítica Especial"
            "rare" -> "Rara"
            "uncommon" -> "Incomum"
            "common" -> "Comum"
            else -> rarity.replaceFirstChar { it.uppercase() }
        }

    /**
     * Formatted string of key formats legality (Commander, Modern, Standard, Legacy, Pauper)
     */
    fun getFormattedLegalities(): List<Pair<String, Boolean>> {
        val legs = legalities ?: return emptyList()
        val keyFormats = listOf("commander", "modern", "standard", "pauper", "legacy", "vintage")
        return keyFormats.mapNotNull { format ->
            val status = legs[format]
            if (status != null) {
                val isLegal = status.equals("legal", ignoreCase = true)
                format.replaceFirstChar { it.uppercase() } to isLegal
            } else null
        }
    }
}

/**
 * Image URIs for different resolutions provided by Scryfall
 */
@JsonClass(generateAdapter = true)
data class ScryfallImageUris(
    @Json(name = "small") val small: String? = null,
    @Json(name = "normal") val normal: String? = null,
    @Json(name = "large") val large: String? = null,
    @Json(name = "png") val png: String? = null,
    @Json(name = "art_crop") val artCrop: String? = null,
    @Json(name = "border_crop") val borderCrop: String? = null
)

/**
 * Price points provided by Scryfall in USD and EUR
 */
@JsonClass(generateAdapter = true)
data class ScryfallPrices(
    @Json(name = "usd") val usd: String? = null,
    @Json(name = "usd_foil") val usdFoil: String? = null,
    @Json(name = "usd_etched") val usdEtched: String? = null,
    @Json(name = "eur") val eur: String? = null,
    @Json(name = "eur_foil") val eurFoil: String? = null,
    @Json(name = "tix") val tix: String? = null
)

/**
 * Sub-card face for double-faced / MDFC / flip cards
 */
@JsonClass(generateAdapter = true)
data class ScryfallCardFace(
    @Json(name = "name") val name: String = "",
    @Json(name = "mana_cost") val manaCost: String? = null,
    @Json(name = "type_line") val typeLine: String? = null,
    @Json(name = "oracle_text") val oracleText: String? = null,
    @Json(name = "printed_name") val printedName: String? = null,
    @Json(name = "printed_type_line") val printedTypeLine: String? = null,
    @Json(name = "printed_text") val printedText: String? = null,
    @Json(name = "power") val power: String? = null,
    @Json(name = "toughness") val toughness: String? = null,
    @Json(name = "loyalty") val loyalty: String? = null,
    @Json(name = "flavor_text") val flavorText: String? = null,
    @Json(name = "artist") val artist: String? = null,
    @Json(name = "image_uris") val imageUris: ScryfallImageUris? = null
)
