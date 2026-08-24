package com.example.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TcgdexCardBrief(
    @Json(name = "id") val id: String = "",
    @Json(name = "localId") val localId: String = "",
    @Json(name = "name") val name: String = "",
    @Json(name = "image") val image: String? = null
) {
    fun getHighResImage(): String? {
        if (image.isNullOrBlank()) return null
        return if (image.endsWith("/high.png") || image.endsWith(".png") || image.endsWith(".jpg")) {
            image
        } else {
            "$image/high.png"
        }
    }
}

@JsonClass(generateAdapter = true)
data class TcgdexCardDetail(
    @Json(name = "id") val id: String = "",
    @Json(name = "localId") val localId: String = "",
    @Json(name = "name") val name: String = "",
    @Json(name = "category") val category: String? = null,
    @Json(name = "illustrator") val illustrator: String? = null,
    @Json(name = "rarity") val rarity: String? = null,
    @Json(name = "hp") val hp: Int? = null,
    @Json(name = "types") val types: List<String>? = null,
    @Json(name = "stage") val stage: String? = null,
    @Json(name = "image") val image: String? = null,
    @Json(name = "set") val set: TcgdexSetBrief? = null,
    @Json(name = "variants") val variants: TcgdexVariants? = null
) {
    fun getHighResImage(): String? {
        if (image.isNullOrBlank()) return null
        return if (image.endsWith("/high.png") || image.endsWith(".png") || image.endsWith(".jpg")) {
            image
        } else {
            "$image/high.png"
        }
    }

    fun getEstimatedPriceBrl(): Double {
        val r = (rarity ?: "").lowercase()
        return when {
            r.contains("special illustration") || r.contains("secret") || r.contains("sar") -> 450.0
            r.contains("illustration rare") || r.contains("art rare") || r.contains("ar") -> 120.0
            r.contains("ultra rare") || r.contains("full art") -> 85.0
            r.contains("double rare") || r.contains("ex") || r.contains("vmax") -> 45.0
            r.contains("holo") || r.contains("rare") -> 18.0
            r.contains("uncommon") || r.contains("incomum") -> 5.0
            else -> 2.50
        }
    }
}

@JsonClass(generateAdapter = true)
data class TcgdexSetBrief(
    @Json(name = "id") val id: String = "",
    @Json(name = "name") val name: String = "",
    @Json(name = "logo") val logo: String? = null,
    @Json(name = "symbol") val symbol: String? = null,
    @Json(name = "cardCount") val cardCount: TcgdexCardCount? = null
)

@JsonClass(generateAdapter = true)
data class TcgdexCardCount(
    @Json(name = "total") val total: Int? = null,
    @Json(name = "official") val official: Int? = null
)

@JsonClass(generateAdapter = true)
data class TcgdexVariants(
    @Json(name = "normal") val normal: Boolean = false,
    @Json(name = "reverse") val reverse: Boolean = false,
    @Json(name = "holo") val holo: Boolean = false,
    @Json(name = "firstEdition") val firstEdition: Boolean = false
)
