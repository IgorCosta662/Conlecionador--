package com.example.api

import com.example.data.*
import kotlin.math.roundToInt
import kotlin.random.Random

interface PriceSource {
    val sourceId: String
    val displayName: String
    val categorySupported: List<String>
    val isOfficialRetail: Boolean
}

object PriceSourceRegistry {
    val sources = listOf(
        object : PriceSource {
            override val sourceId = "ligapokemon"
            override val displayName = "LigaPokémon / LigaMagic (Brasil)"
            override val categorySupported = listOf("Pokémon TCG", "Magic: The Gathering", "Yu-Gi-Oh!", "One Piece Card Game")
            override val isOfficialRetail = false
        },
        object : PriceSource {
            override val sourceId = "tcgplayer"
            override val displayName = "TCGPlayer (EUA)"
            override val categorySupported = listOf("Pokémon TCG", "Magic: The Gathering", "Yu-Gi-Oh!", "One Piece Card Game", "Digimon Card Game")
            override val isOfficialRetail = false
        },
        object : PriceSource {
            override val sourceId = "cardmarket"
            override val displayName = "Cardmarket (Europa)"
            override val categorySupported = listOf("Pokémon TCG", "Magic: The Gathering", "Yu-Gi-Oh!", "One Piece Card Game")
            override val isOfficialRetail = false
        },
        object : PriceSource {
            override val sourceId = "yuyutei"
            override val displayName = "Yuyu-tei / Mercari (Japão)"
            override val categorySupported = listOf("Pokémon TCG", "One Piece Card Game", "Yu-Gi-Oh!", "Tomica")
            override val isOfficialRetail = false
        },
        object : PriceSource {
            override val sourceId = "ebay"
            override val displayName = "eBay (Global)"
            override val categorySupported = listOf("Hot Wheels", "Matchbox", "Tomica", "Majorette", "Action figures", "Moedas", "Pokémon TCG", "Magic: The Gathering")
            override val isOfficialRetail = false
        },
        object : PriceSource {
            override val sourceId = "mercadolivre"
            override val displayName = "Mercado Livre (Brasil)"
            override val categorySupported = listOf("Hot Wheels", "Matchbox", "Tomica", "Action figures", "Moedas", "Pokémon TCG", "Magic: The Gathering", "Yu-Gi-Oh!")
            override val isOfficialRetail = false
        }
    )

    fun generateRealisticOffersAndHistory(
        itemName: String,
        subCategory: String = "",
        rarity: String = "Comum",
        variant: String = "Normal",
        condition: String = "Near Mint",
        language: String = "PT-BR",
        marketRegion: MarketRegion = MarketRegion.BRAZIL,
        baseEstimatedPrice: Double = 0.0
    ): Pair<List<PriceOffer>, List<PriceHistoryPoint>> {
        val base = if (baseEstimatedPrice > 0.0) baseEstimatedPrice else 35.0

        val applicableSources = when (marketRegion) {
            MarketRegion.BRAZIL -> when {
                subCategory.contains("Pokémon", ignoreCase = true) -> listOf("LigaPokémon (Brasil)", "Mercado Livre Coleções", "Shopee Brasil (Verificado)", "Comunidade TCG SP")
                subCategory.contains("Magic", ignoreCase = true) -> listOf("LigaMagic (Brasil)", "Mercado Livre", "Bazar de Bagdá")
                subCategory.contains("Yu-Gi-Oh", ignoreCase = true) -> listOf("LigaYugioh (Brasil)", "Mercado Livre", "MypCards BR")
                subCategory.contains("Hot Wheels", ignoreCase = true) -> listOf("Mercado Livre Colecionadores", "Grupos HW Brasil", "Encontros Diecast SP", "Lojas Especializadas")
                else -> listOf("Mercado Livre Brasil", "Shopee Coleções", "Lojas Especializadas")
            }
            MarketRegion.USA -> when {
                subCategory.contains("Card", ignoreCase = true) || subCategory.contains("Pokémon", ignoreCase = true) -> listOf("TCGPlayer (US Market)", "eBay US Sold Listings", "Troll and Toad", "Dave & Adam's")
                subCategory.contains("Hot Wheels", ignoreCase = true) -> listOf("eBay US Diecast", "HWC / RLC Forums", "The Diecast Mall")
                else -> listOf("eBay US", "Amazon US Collectibles")
            }
            MarketRegion.JAPAN -> listOf("Yuyu-tei (Japão)", "Mercari JP / Buyee", "Hareruya Japan", "Mandarake Tokyo")
            MarketRegion.EUROPE -> listOf("Cardmarket (EU)", "eBay UK/DE", "Gate to the Games")
        }

        val offers = mutableListOf<PriceOffer>()
        val rnd = Random(itemName.hashCode() + variant.hashCode() + language.hashCode() + marketRegion.hashCode())

        val variationMultipliers = listOf(0.88, 0.95, 1.04, 1.15)
        applicableSources.take(3 + rnd.nextInt(2)).forEachIndexed { index, sourceName ->
            val mult = variationMultipliers.getOrElse(index) { 1.0 + (rnd.nextDouble() * 0.2 - 0.1) }
            val price = ((base * mult) * 100.0).roundToInt() / 100.0
            val condText = when {
                condition.contains("Lacrado", ignoreCase = true) -> "Novo / Lacrado"
                condition.contains("Graded", ignoreCase = true) || condition.contains("PSA", ignoreCase = true) -> "Graduada (Slab)"
                variant.contains("Foil", ignoreCase = true) || variant.contains("Holo", ignoreCase = true) -> "Near Mint (Foil/Holo)"
                variant.contains("Super Treasure", ignoreCase = true) -> "Cartela Curta / Lacrado (STH)"
                else -> if (index == 0) "Near Mint" else if (index == 1) "Lightly Played" else "Near Mint"
            }
            offers.add(
                PriceOffer(
                    storeName = sourceName,
                    priceInBRL = price,
                    condition = condText,
                    listingType = if (index == 0) "Menor Preço Ativo" else if (index == 1) "Média de Anúncios" else "Última Venda Concluída",
                    isVerified = true
                )
            )
        }

        val history = listOf(
            PriceHistoryPoint("Mai/2026", ((base * 0.86) * 10).roundToInt() / 10.0),
            PriceHistoryPoint("Jun/2026", ((base * 0.91) * 10).roundToInt() / 10.0),
            PriceHistoryPoint("Jul/2026", ((base * 0.96) * 10).roundToInt() / 10.0),
            PriceHistoryPoint("Ago/2026", ((base * 1.0) * 10).roundToInt() / 10.0)
        )

        return Pair(offers, history)
    }

    fun generateConditionPriceTiers(baseNearMintPrice: Double): List<ConditionPriceTier> {
        val base = if (baseNearMintPrice > 0.0) baseNearMintPrice else 50.0
        return listOf(
            ConditionPriceTier("Mint / Gem Mint", ((base * 1.35) * 10.0).roundToInt() / 10.0),
            ConditionPriceTier("Near Mint (NM)", base),
            ConditionPriceTier("Excellent / Lightly Played", ((base * 0.80) * 10.0).roundToInt() / 10.0),
            ConditionPriceTier("Good / Moderately Played", ((base * 0.60) * 10.0).roundToInt() / 10.0),
            ConditionPriceTier("Played / Heavily Played", ((base * 0.40) * 10.0).roundToInt() / 10.0),
            ConditionPriceTier("Poor / Damaged", ((base * 0.22) * 10.0).roundToInt() / 10.0)
        )
    }

    fun generateLanguageComparisons(
        cardName: String,
        basePriceInCurrentLanguage: Double,
        currentLanguage: String
    ): List<LanguagePriceComparison> {
        val base = if (basePriceInCurrentLanguage > 0.0) basePriceInCurrentLanguage else 80.0

        // Ratio estimates standard across TCGs (e.g. English is usually ~1.4x - 1.8x BRL, Japanese varies between 0.9x - 1.3x)
        val ptPrice = when (currentLanguage.uppercase()) {
            "PT-BR", "PT" -> base
            "EN" -> base * 0.65
            "JP", "JA" -> base * 0.85
            else -> base * 0.75
        }
        val enPrice = when (currentLanguage.uppercase()) {
            "EN" -> base
            "PT-BR", "PT" -> base * 1.55
            "JP", "JA" -> base * 1.30
            else -> base * 1.40
        }
        val jpPrice = when (currentLanguage.uppercase()) {
            "JP", "JA" -> base
            "PT-BR", "PT" -> base * 1.15
            "EN" -> base * 0.78
            else -> base * 1.05
        }

        return listOf(
            LanguagePriceComparison(
                languageCode = "PT-BR",
                languageName = "Português",
                flag = "PT-BR",
                averagePriceBrl = ((ptPrice) * 10.0).roundToInt() / 10.0,
                minPriceBrl = ((ptPrice * 0.82) * 10.0).roundToInt() / 10.0,
                maxPriceBrl = ((ptPrice * 1.25) * 10.0).roundToInt() / 10.0
            ),
            LanguagePriceComparison(
                languageCode = "EN",
                languageName = "Inglês",
                flag = "EN",
                averagePriceBrl = ((enPrice) * 10.0).roundToInt() / 10.0,
                minPriceBrl = ((enPrice * 0.85) * 10.0).roundToInt() / 10.0,
                maxPriceBrl = ((enPrice * 1.28) * 10.0).roundToInt() / 10.0
            ),
            LanguagePriceComparison(
                languageCode = "JP",
                languageName = "Japonês",
                flag = "JP",
                averagePriceBrl = ((jpPrice) * 10.0).roundToInt() / 10.0,
                minPriceBrl = ((jpPrice * 0.80) * 10.0).roundToInt() / 10.0,
                maxPriceBrl = ((jpPrice * 1.22) * 10.0).roundToInt() / 10.0
            )
        )
    }
}

