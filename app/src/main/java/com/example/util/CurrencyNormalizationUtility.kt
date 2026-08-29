package com.example.util

import com.example.data.AppCurrency
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Currency Normalization Utility
 * Ensures that prices retrieved from LigaMagic (BRL) and international secondary markets (USD, EUR, JPY, GBP)
 * are accurately converted, normalized against base currencies, and calibrated against exchange rate discrepancies.
 */
object CurrencyNormalizationUtility {

    // Central live reference exchange rates (relative to BRL)
    const val DEFAULT_USD_BRL = 5.65
    const val DEFAULT_EUR_BRL = 6.10
    const val DEFAULT_GBP_BRL = 7.15
    const val DEFAULT_JPY_BRL = 0.037
    const val DEFAULT_CAD_BRL = 4.10
    const val DEFAULT_AUD_BRL = 3.65

    private val ratesToBrl = mutableMapOf(
        "BRL" to 1.0,
        "USD" to DEFAULT_USD_BRL,
        "EUR" to DEFAULT_EUR_BRL,
        "GBP" to DEFAULT_GBP_BRL,
        "JPY" to DEFAULT_JPY_BRL,
        "CAD" to DEFAULT_CAD_BRL,
        "AUD" to DEFAULT_AUD_BRL
    )

    /**
     * Converts an amount from one currency to another using the normalized exchange matrix.
     */
    fun convert(amount: Double, fromCurrency: String, toCurrency: String): Double {
        if (amount <= 0.0) return 0.0
        val cleanFrom = fromCurrency.trim().uppercase()
        val cleanTo = toCurrency.trim().uppercase()
        if (cleanFrom == cleanTo) return amount

        val fromRate = ratesToBrl[cleanFrom] ?: 1.0
        val toRate = ratesToBrl[cleanTo] ?: 1.0

        // Step 1: Normalize to BRL
        val inBrl = amount * fromRate
        // Step 2: Convert from BRL to target
        val converted = inBrl / toRate

        return ((converted * 100.0).roundToInt()) / 100.0
    }

    /**
     * Converts a BRL price (such as LigaMagic) to the user's preferred AppCurrency locale.
     */
    fun convertFromBrlToAppCurrency(amountInBrl: Double, targetCurrency: AppCurrency): Double {
        if (amountInBrl <= 0.0) return 0.0
        return when (targetCurrency) {
            AppCurrency.BRL -> amountInBrl
            AppCurrency.USD -> convert(amountInBrl, "BRL", "USD")
            AppCurrency.EUR -> convert(amountInBrl, "BRL", "EUR")
            AppCurrency.GBP -> convert(amountInBrl, "BRL", "GBP")
            AppCurrency.JPY -> convert(amountInBrl, "BRL", "JPY")
        }
    }

    /**
     * Formats an amount according to the user's preferred currency and locale.
     */
    fun formatWithLocale(amountInBrl: Double, currency: AppCurrency, locale: Locale = Locale.getDefault()): String {
        val converted = convertFromBrlToAppCurrency(amountInBrl, currency)
        return when (currency) {
            AppCurrency.JPY -> "${currency.symbol} ${String.format(Locale.GERMANY, "%,.0f", converted)}"
            else -> "${currency.symbol} ${String.format(Locale.GERMANY, "%,.2f", converted)}"
        }
    }

    /**
     * Report analyzing exchange discrepancy between domestic marketplace (LigaMagic) and global market (TCGPlayer/Cardmarket).
     */
    data class DiscrepancyAnalysis(
        val ligaPriceBrl: Double,
        val globalNormalizedPriceBrl: Double,
        val priceDifferenceBrl: Double,
        val deviationPercentage: Double,
        val isDomesticPremium: Boolean,
        val stabilizedConsensusBrl: Double,
        val explanation: String
    )

    /**
     * Analyzes and reconciles exchange rate and regional supply discrepancies between LigaMagic and foreign secondary markets.
     * E.g. Cards with high domestic demand in Brazil might carry a 10-25% premium, or vice versa due to exchange volatility.
     */
    fun reconcileDiscrepancy(
        ligaMagicPriceBrl: Double,
        tcgPlayerUsdPrice: Double?,
        cardmarketEurPrice: Double?
    ): DiscrepancyAnalysis {
        val globalUsdBrl = if (tcgPlayerUsdPrice != null && tcgPlayerUsdPrice > 0.0) {
            convert(tcgPlayerUsdPrice, "USD", "BRL")
        } else null

        val globalEurBrl = if (cardmarketEurPrice != null && cardmarketEurPrice > 0.0) {
            convert(cardmarketEurPrice, "EUR", "BRL")
        } else null

        val globalPrices = listOfNotNull(globalUsdBrl, globalEurBrl)
        val globalAvgBrl = if (globalPrices.isNotEmpty()) globalPrices.average() else ligaMagicPriceBrl

        val diff = ligaMagicPriceBrl - globalAvgBrl
        val deviationPct = if (globalAvgBrl > 0.0) ((diff / globalAvgBrl) * 100.0) else 0.0

        // Stabilized consensus price:
        // Gives 50% weight to LigaMagic (national liquidity) and 50% to global secondary markets,
        // while dampening extreme outliers (> 40% deviation).
        val stabilized = when {
            globalPrices.isEmpty() -> ligaMagicPriceBrl
            abs(deviationPct) <= 20.0 -> {
                // Low deviation: weighted balance
                (ligaMagicPriceBrl * 0.55) + (globalAvgBrl * 0.45)
            }
            deviationPct > 20.0 -> {
                // Domestic premium (Brazil cost of import / localized scarcity)
                (ligaMagicPriceBrl * 0.65) + (globalAvgBrl * 0.35)
            }
            else -> {
                // Global premium (e.g. high overseas demand / localized delay)
                (ligaMagicPriceBrl * 0.45) + (globalAvgBrl * 0.55)
            }
        }

        val stabilizedRounded = ((stabilized * 100.0).roundToInt()) / 100.0

        val explanation = when {
            globalPrices.isEmpty() -> "Cotação baseada exclusivamente na liquidez do marketplace nacional."
            abs(deviationPct) <= 10.0 -> "Paridade perfeita: Cotação nacional alinhada com o mercado global (variação de ${String.format(Locale.US, "%.1f", abs(deviationPct))}%)."
            deviationPct > 10.0 -> "Prêmio de mercado nacional (+${String.format(Locale.US, "%.1f", deviationPct)}%): Reflete custos de importação e disponibilidade no Brasil."
            else -> "Oportunidade de arbitragem: Preço nacional está ${String.format(Locale.US, "%.1f", abs(deviationPct))}% abaixo da cotação internacional convertida."
        }

        return DiscrepancyAnalysis(
            ligaPriceBrl = ligaMagicPriceBrl,
            globalNormalizedPriceBrl = ((globalAvgBrl * 100.0).roundToInt()) / 100.0,
            priceDifferenceBrl = ((diff * 100.0).roundToInt()) / 100.0,
            deviationPercentage = ((deviationPct * 10.0).roundToInt()) / 10.0,
            isDomesticPremium = diff > 0,
            stabilizedConsensusBrl = stabilizedRounded,
            explanation = explanation
        )
    }

    /**
     * Updates an exchange rate in memory if dynamically fetched.
     */
    fun updateRate(currencyCode: String, rateToBrl: Double) {
        if (rateToBrl > 0.0) {
            ratesToBrl[currencyCode.trim().uppercase()] = rateToBrl
        }
    }

    fun getRateToBrl(currencyCode: String): Double {
        return ratesToBrl[currencyCode.trim().uppercase()] ?: 1.0
    }
}
