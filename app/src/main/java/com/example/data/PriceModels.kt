package com.example.data

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

enum class AppCurrency(
    val code: String,
    val symbol: String,
    val namePt: String,
    val approximateRateToBRL: Double // 1 unit in BRL
) {
    BRL("BRL", "R$", "Real Brasileiro", 1.0),
    USD("USD", "$", "Dólar Americano", 5.45),
    EUR("EUR", "€", "Euro", 5.95),
    GBP("GBP", "£", "Libra Esterlina", 6.95);

    fun convertFromBRL(amountInBrl: Double): Double {
        return amountInBrl / approximateRateToBRL
    }

    fun convertToBRL(amount: Double): Double {
        return amount * approximateRateToBRL
    }

    fun format(amountInBrl: Double): String {
        val converted = convertFromBRL(amountInBrl)
        return "$symbol ${String.format(java.util.Locale.GERMANY, "%,.2f", converted)}"
    }

    fun formatExact(amount: Double): String {
        return "$symbol ${String.format(java.util.Locale.GERMANY, "%,.2f", amount)}"
    }
}

@JsonClass(generateAdapter = true)
data class PriceOffer(
    val storeName: String,
    val priceInBRL: Double,
    val originalPrice: Double = priceInBRL,
    val originalCurrency: String = "BRL",
    val condition: String = "Near Mint",
    val listingType: String = "Anúncio Ativo",
    val isVerified: Boolean = true,
    val url: String? = null
)

@JsonClass(generateAdapter = true)
data class PriceHistoryPoint(
    val period: String,
    val priceInBRL: Double,
    val timestamp: Long = System.currentTimeMillis()
)

object JsonParserHelper {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val priceOffersListType = Types.newParameterizedType(List::class.java, PriceOffer::class.java)
    private val priceHistoryListType = Types.newParameterizedType(List::class.java, PriceHistoryPoint::class.java)

    private val priceOffersAdapter = moshi.adapter<List<PriceOffer>>(priceOffersListType)
    private val priceHistoryAdapter = moshi.adapter<List<PriceHistoryPoint>>(priceHistoryListType)

    fun offersToJson(offers: List<PriceOffer>): String {
        return try {
            priceOffersAdapter.toJson(offers)
        } catch (e: Exception) {
            "[]"
        }
    }

    fun offersFromJson(json: String?): List<PriceOffer> {
        if (json.isNullOrBlank()) return emptyList()
        return try {
            priceOffersAdapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun historyToJson(history: List<PriceHistoryPoint>): String {
        return try {
            priceHistoryAdapter.toJson(history)
        } catch (e: Exception) {
            "[]"
        }
    }

    fun historyFromJson(json: String?): List<PriceHistoryPoint> {
        if (json.isNullOrBlank()) return emptyList()
        return try {
            priceHistoryAdapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
