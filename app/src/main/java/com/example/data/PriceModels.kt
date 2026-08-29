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
    USD("USD", "US$", "Dólar Americano", 5.45),
    EUR("EUR", "€", "Euro", 5.95),
    GBP("GBP", "£", "Libra Esterlina", 6.95),
    JPY("JPY", "¥", "Iene Japonês", 0.036);

    fun convertFromBRL(amountInBrl: Double): Double {
        return amountInBrl / approximateRateToBRL
    }

    fun convertToBRL(amount: Double): Double {
        return amount * approximateRateToBRL
    }

    fun format(amountInBrl: Double): String {
        val converted = convertFromBRL(amountInBrl)
        val absVal = kotlin.math.abs(converted)
        val sign = if (converted < -0.001) "-" else ""
        return if (this == JPY) {
            "$sign$symbol ${String.format(java.util.Locale.GERMANY, "%,.0f", absVal)}"
        } else {
            "$sign$symbol ${String.format(java.util.Locale.GERMANY, "%,.2f", absVal)}"
        }
    }

    fun formatSigned(amountInBrl: Double): String {
        val converted = convertFromBRL(amountInBrl)
        val absVal = kotlin.math.abs(converted)
        val sign = if (converted > 0.001) "+" else if (converted < -0.001) "-" else ""
        return if (this == JPY) {
            "$sign$symbol ${String.format(java.util.Locale.GERMANY, "%,.0f", absVal)}"
        } else {
            "$sign$symbol ${String.format(java.util.Locale.GERMANY, "%,.2f", absVal)}"
        }
    }

    fun formatValue(amountInBrl: Double): String = format(amountInBrl)

    fun formatExact(amount: Double): String {
        val absVal = kotlin.math.abs(amount)
        val sign = if (amount < -0.001) "-" else ""
        return if (this == JPY) {
            "$sign$symbol ${String.format(java.util.Locale.GERMANY, "%,.0f", absVal)}"
        } else {
            "$sign$symbol ${String.format(java.util.Locale.GERMANY, "%,.2f", absVal)}"
        }
    }
}

// ----------------------------------------------------
// Market Region (Brasil, EUA, Japão, Europa)
// ----------------------------------------------------
enum class MarketRegion(
    val code: String,
    val displayName: String,
    val flag: String,
    val primarySource: String
) {
    BRAZIL("BR", "Brasil", "BR", "LigaPokémon / Mercado Livre"),
    USA("US", "Estados Unidos", "US", "TCGPlayer / eBay US"),
    JAPAN("JP", "Japão", "JP", "Mercari JP / Yuyu-tei"),
    EUROPE("EU", "Europa", "EU", "Cardmarket EU");

    companion object {
        fun fromCode(code: String): MarketRegion =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: BRAZIL
    }
}

// ----------------------------------------------------
// Language Support
// ----------------------------------------------------
data class SupportedLanguage(
    val code: String,
    val namePt: String,
    val flag: String
)

object LanguageRegistry {
    val languages = listOf(
        SupportedLanguage("PT-BR", "Português", "PT-BR"),
        SupportedLanguage("EN", "Inglês", "EN"),
        SupportedLanguage("JP", "Japonês", "JP"),
        SupportedLanguage("ZH", "Chinês", "ZH"),
        SupportedLanguage("KO", "Coreano", "KO"),
        SupportedLanguage("FR", "Francês", "FR"),
        SupportedLanguage("DE", "Alemão", "DE"),
        SupportedLanguage("ES", "Espanhol", "ES"),
        SupportedLanguage("IT", "Italiano", "IT"),
        SupportedLanguage("N/A", "Universal / Diecast", "INT")
    )

    fun getFlag(code: String): String {
        val clean = code.trim().uppercase()
        return languages.firstOrNull {
            it.code.equals(clean, ignoreCase = true) ||
            clean.contains(it.code) ||
            it.namePt.equals(clean, ignoreCase = true)
        }?.flag ?: "INT"
    }

    fun getDisplayName(code: String): String {
        val clean = code.trim().uppercase()
        val found = languages.firstOrNull {
            it.code.equals(clean, ignoreCase = true) ||
            clean.contains(it.code) ||
            it.namePt.equals(clean, ignoreCase = true)
        }
        return if (found != null) "[${found.code}] ${found.namePt}" else code
    }
}

// ----------------------------------------------------
// Price Comparison across Languages
// ----------------------------------------------------
@JsonClass(generateAdapter = true)
data class LanguagePriceComparison(
    val languageCode: String,
    val languageName: String,
    val flag: String,
    val averagePriceBrl: Double,
    val minPriceBrl: Double,
    val maxPriceBrl: Double
)

// ----------------------------------------------------
// Condition Assessment Breakdown
// ----------------------------------------------------
@JsonClass(generateAdapter = true)
data class ConditionAssessment(
    val estimatedCondition: String = "Near Mint",
    val confidencePercent: Int = 85,
    val scratches: String = "Sem arranhões aparentes",
    val edges: String = "Bordas regulares e preservadas",
    val corners: String = "Cantos firmes sem desgaste visível",
    val centering: String = "Boa centralização estimada (aprox. 55/45)",
    val bends: String = "Superfície perfeitamente plana",
    val notes: String = "Condição estimada por IA com base na análise visual da imagem."
)

// ----------------------------------------------------
// Cross-Referenced Market Source Quote
// ----------------------------------------------------
@JsonClass(generateAdapter = true)
data class MarketSourceQuote(
    val sourceName: String, // e.g. "LigaMagic Brasil (Média)", "TCGPlayer Direct (US)", "Cardmarket Trend (EU)"
    val originalPrice: Double,
    val originalCurrency: String, // "BRL", "USD", "EUR", "JPY"
    val normalizedPriceBrl: Double,
    val weightPercentage: Int, // e.g. 50%
    val condition: String = "Near Mint",
    val isDomesticSource: Boolean = false,
    val sourceRegion: String = "BR" // "BR", "US", "EU", "JP"
)

// ----------------------------------------------------
// Cross-Referenced Market Report
// ----------------------------------------------------
@JsonClass(generateAdapter = true)
data class CrossReferencedPriceReport(
    val stableAveragedPriceBrl: Double,
    val minPriceBrl: Double,
    val maxPriceBrl: Double,
    val stabilityScore: Int = 92, // 0-100%
    val marketSpreadPercent: Double = 12.5,
    val discrepancyNote: String = "Paridade equilibrada entre LigaMagic e mercados internacionais.",
    val quotes: List<MarketSourceQuote> = emptyList()
)

// ----------------------------------------------------
// Price Matrix by Condition
// ----------------------------------------------------
@JsonClass(generateAdapter = true)
data class ConditionPriceTier(
    val condition: String,
    val priceBrl: Double
)

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
    private val langComparisonListType = Types.newParameterizedType(List::class.java, LanguagePriceComparison::class.java)
    private val conditionTiersListType = Types.newParameterizedType(List::class.java, ConditionPriceTier::class.java)
    private val stringListType = Types.newParameterizedType(List::class.java, String::class.java)

    private val priceOffersAdapter = moshi.adapter<List<PriceOffer>>(priceOffersListType)
    private val priceHistoryAdapter = moshi.adapter<List<PriceHistoryPoint>>(priceHistoryListType)
    private val langComparisonAdapter = moshi.adapter<List<LanguagePriceComparison>>(langComparisonListType)
    private val conditionTiersAdapter = moshi.adapter<List<ConditionPriceTier>>(conditionTiersListType)
    private val conditionAssessmentAdapter = moshi.adapter(ConditionAssessment::class.java)
    private val crossReferencedReportAdapter = moshi.adapter(CrossReferencedPriceReport::class.java)
    private val stringListAdapter = moshi.adapter<List<String>>(stringListType)

    fun offersToJson(offers: List<PriceOffer>): String = try { priceOffersAdapter.toJson(offers) } catch (e: Exception) { "[]" }
    fun offersFromJson(json: String?): List<PriceOffer> = if (json.isNullOrBlank()) emptyList() else try { priceOffersAdapter.fromJson(json) ?: emptyList() } catch (e: Exception) { emptyList() }

    fun crossReferencedReportToJson(report: CrossReferencedPriceReport): String = try { crossReferencedReportAdapter.toJson(report) } catch (e: Exception) { "{}" }
    fun crossReferencedReportFromJson(json: String?): CrossReferencedPriceReport? = if (json.isNullOrBlank() || json == "{}") null else try { crossReferencedReportAdapter.fromJson(json) } catch (e: Exception) { null }

    fun historyToJson(history: List<PriceHistoryPoint>): String = try { priceHistoryAdapter.toJson(history) } catch (e: Exception) { "[]" }
    fun historyFromJson(json: String?): List<PriceHistoryPoint> = if (json.isNullOrBlank()) emptyList() else try { priceHistoryAdapter.fromJson(json) ?: emptyList() } catch (e: Exception) { emptyList() }

    fun langComparisonToJson(list: List<LanguagePriceComparison>): String = try { langComparisonAdapter.toJson(list) } catch (e: Exception) { "[]" }
    fun langComparisonFromJson(json: String?): List<LanguagePriceComparison> = if (json.isNullOrBlank()) emptyList() else try { langComparisonAdapter.fromJson(json) ?: emptyList() } catch (e: Exception) { emptyList() }

    fun conditionTiersToJson(tiers: List<ConditionPriceTier>): String = try { conditionTiersAdapter.toJson(tiers) } catch (e: Exception) { "[]" }
    fun conditionTiersFromJson(json: String?): List<ConditionPriceTier> = if (json.isNullOrBlank()) emptyList() else try { conditionTiersAdapter.fromJson(json) ?: emptyList() } catch (e: Exception) { emptyList() }

    fun conditionAssessmentToJson(assessment: ConditionAssessment): String = try { conditionAssessmentAdapter.toJson(assessment) } catch (e: Exception) { "{}" }
    fun conditionAssessmentFromJson(json: String?): ConditionAssessment = if (json.isNullOrBlank() || json == "{}") ConditionAssessment() else try { conditionAssessmentAdapter.fromJson(json) ?: ConditionAssessment() } catch (e: Exception) { ConditionAssessment() }

    fun stringListToJson(list: List<String>): String = try { stringListAdapter.toJson(list) } catch (e: Exception) { "[]" }
    fun stringListFromJson(json: String?): List<String> = if (json.isNullOrBlank()) emptyList() else try { stringListAdapter.fromJson(json) ?: emptyList() } catch (e: Exception) { emptyList() }
}

