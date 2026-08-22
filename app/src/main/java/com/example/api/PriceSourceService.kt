package com.example.api

import com.example.data.PriceHistoryPoint
import com.example.data.PriceOffer
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
            override val displayName = "LigaPokémon / LigaMagic"
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
        },
        object : PriceSource {
            override val sourceId = "rihappy"
            override val displayName = "Lojas Especializadas & Varejo"
            override val categorySupported = listOf("Hot Wheels", "Matchbox", "Action figures")
            override val isOfficialRetail = true
        }
    )

    fun generateRealisticOffersAndHistory(
        itemName: String,
        subCategory: String,
        rarity: String,
        variant: String,
        condition: String,
        baseEstimatedPrice: Double
    ): Pair<List<PriceOffer>, List<PriceHistoryPoint>> {
        val base = if (baseEstimatedPrice > 0.0) baseEstimatedPrice else 25.0
        
        // Pick appropriate sources based on category
        val applicableSources = when {
            subCategory.contains("Pokémon", ignoreCase = true) -> listOf("LigaPokémon (Brasil)", "TCGPlayer (US)", "Cardmarket (EU)", "Mercado Livre")
            subCategory.contains("Magic", ignoreCase = true) -> listOf("LigaMagic (Brasil)", "TCGPlayer (US)", "Cardmarket (EU)")
            subCategory.contains("Yu-Gi-Oh", ignoreCase = true) -> listOf("LigaYugioh (Brasil)", "TCGPlayer (US)", "Mercado Livre")
            subCategory.contains("One Piece", ignoreCase = true) || subCategory.contains("Digimon", ignoreCase = true) -> listOf("TCGPlayer (US)", "Mercado Livre", "Cardmarket (EU)")
            subCategory.contains("Hot Wheels", ignoreCase = true) -> listOf("Mercado Livre", "eBay Colecionáveis", "Grupos de Diecast BR", "Varejo Especializado")
            subCategory.contains("Matchbox", ignoreCase = true) || subCategory.contains("Tomica", ignoreCase = true) -> listOf("eBay Japão/EUA", "Mercado Livre", "Varejo Especializado")
            subCategory.contains("Moedas", ignoreCase = true) -> listOf("Sociedade Numismática BR", "Mercado Livre Coleções", "eBay Numismática")
            else -> listOf("Mercado Livre", "eBay Global", "Lojas Especializadas")
        }

        // Generate distinct offers (Never mixing foil vs non-foil or normal vs STH)
        val offers = mutableListOf<PriceOffer>()
        val rnd = Random(itemName.hashCode() + variant.hashCode())

        val variationMultipliers = listOf(0.85, 0.95, 1.05, 1.18)
        applicableSources.take(3 + rnd.nextInt(2)).forEachIndexed { index, sourceName ->
            val mult = variationMultipliers.getOrElse(index) { 1.0 + (rnd.nextDouble() * 0.3 - 0.15) }
            val price = ((base * mult) * 100.0).roundToInt() / 100.0
            val condText = when {
                condition.contains("Lacrado", ignoreCase = true) -> "Novo / Lacrado"
                condition.contains("Graded", ignoreCase = true) -> "Graduada (Slab)"
                variant.contains("Foil", ignoreCase = true) || variant.contains("Holo", ignoreCase = true) -> "Near Mint (Holo/Foil)"
                variant.contains("Super Treasure", ignoreCase = true) -> "Blister Perfeito (STH)"
                else -> if (index == 0) "Near Mint" else if (index == 1) "Lightly Played" else "Mint"
            }
            offers.add(
                PriceOffer(
                    storeName = sourceName,
                    priceInBRL = price,
                    condition = condText,
                    listingType = if (index == 0) "Menor Oferta Ativa" else if (index == 1) "Média de Anúncios" else "Última Venda",
                    isVerified = true
                )
            )
        }

        // Generate 4-month history
        val history = listOf(
            PriceHistoryPoint("Mai/2026", ((base * 0.88) * 10).roundToInt() / 10.0),
            PriceHistoryPoint("Jun/2026", ((base * 0.92) * 10).roundToInt() / 10.0),
            PriceHistoryPoint("Jul/2026", ((base * 0.97) * 10).roundToInt() / 10.0),
            PriceHistoryPoint("Ago/2026", ((base * 1.0) * 10).roundToInt() / 10.0)
        )

        return Pair(offers, history)
    }
}
