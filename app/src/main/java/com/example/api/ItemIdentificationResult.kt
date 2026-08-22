package com.example.api

import com.example.data.PriceHistoryPoint
import com.example.data.PriceOffer

data class ItemIdentificationResult(
    // 1. IDENTIFICAÇÃO
    val name: String,
    val category: String, // "Trading Cards", "Carrinhos / Diecast", "Action Figures", "Moedas", "Outros"
    val subCategory: String, // "Pokémon TCG", "Magic: The Gathering", "Yu-Gi-Oh!", "Hot Wheels", "Matchbox", etc.
    val collection: String, // Expansão / Coleção / Série
    val itemNumber: String, // Número da carta ou modelo (#151/165, #045/250)
    val edition: String, // Edição / Tiragem (1st Edition, Unlimited, Mainline, etc.)
    val language: String, // PT-BR, EN, JP, N/A
    val rarity: String, // Comum, Incomum, Raro, Super Raro, Ultra Raro, Secret Rare, Treasure Hunt, Super Treasure Hunt, Lendário
    val variant: String, // Normal, Foil / Holográfico, Reverse Holo, Alternate Art, Promo, Redline, etc.
    val isFoil: Boolean = false,
    val apparentCondition: String = "Near Mint", // Lacrado / Novo, Mint, Near Mint, Usado, Graded
    val gradingInfo: String = "", // e.g. "PSA 10", "BGS 9.5"
    val modelYear: String = "",
    val modelColor: String = "",
    val scale: String = "1:64",
    val isSpecialEdition: Boolean = false, // Treasure Hunt, STH, Convention Exclusive, etc.
    val confidenceScore: Int = 90, // 0 - 100

    // 2. PESQUISA DE PREÇOS
    val averagePrice: Double = 0.0, // Preço Médio (BRL)
    val minPrice: Double = 0.0, // Menor Preço Encontrado (BRL)
    val maxPrice: Double = 0.0, // Maior Preço Encontrado (BRL)
    val hasReliableData: Boolean = true,
    val priceNotice: String = "", // "Não foi possível encontrar dados suficientes para calcular um preço confiável." quando aplicável
    val offers: List<PriceOffer> = emptyList(),
    val priceHistory: List<PriceHistoryPoint> = emptyList(),

    // 3. ANÁLISE / METADATA
    val marketTrendComment: String = "",
    val lastUpdateDate: String = "Hoje"
)
