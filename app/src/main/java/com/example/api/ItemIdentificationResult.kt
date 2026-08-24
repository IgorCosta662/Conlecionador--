package com.example.api

import com.example.data.*

data class ItemIdentificationResult(
    // 1. IDENTIFICAÇÃO E ESTRUTURA TCG / COLECIONÁVEIS
    val name: String,
    val category: String, // "Trading Cards", "Carrinhos / Diecast", "Action Figures", "Moedas", "Outros"
    val subCategory: String, // "Pokémon TCG", "Magic: The Gathering", "Yu-Gi-Oh!", "One Piece Card Game", "Hot Wheels", "Matchbox", etc.
    val collection: String, // Expansão / Coleção / Série
    val itemNumber: String, // Número da carta ou modelo (#151/165, #045/250, OP05-060)
    val edition: String, // Edição / Tiragem (1st Edition, Unlimited, Mainline, etc.)
    val language: String = "PT-BR", // PT-BR, EN, JP, ZH, KO, FR, DE, ES, IT, N/A
    val rarity: String, // Comum, Incomum, Raro, Super Raro, Ultra Raro, Secret Rare, Treasure Hunt, Super Treasure Hunt, Lendário
    val variant: String, // Normal, Foil / Holográfico, Reverse Holo, Alternate Art, Promo, Redline, etc.
    val isFoil: Boolean = false,
    
    // TCG Specific Details
    val cardHp: String = "", // Ex: "HP 330"
    val cardArtist: String = "", // Ex: "Mitsuhiro Arita"
    val cardAttacks: String = "", // Ex: "Brave Wing, Explosive Vortex"
    val cardSetSymbol: String = "",

    // Diecast Specific Details
    val modelYear: String = "",
    val modelColor: String = "",
    val scale: String = "1:64",
    val isSpecialEdition: Boolean = false, // Treasure Hunt, STH, Convention Exclusive, etc.

    // 2. AVALIAÇÃO DE ESTADO / CONDIÇÃO VISUAL
    val apparentCondition: String = "Near Mint", // Mint, Near Mint, Excellent, Good, Played, Poor
    val conditionConfidenceScore: Int = 85, // 0 - 100%
    val conditionAssessment: ConditionAssessment = ConditionAssessment(),
    val conditionPrices: List<ConditionPriceTier> = emptyList(),
    val gradingInfo: String = "", // e.g. "PSA 10", "BGS 9.5"

    // 3. ANÁLISE DE AUTENTICIDADE VISUAL (EXPERIMENTAL)
    val authenticityRisk: String = "Baixo risco aparente", // "Baixo risco aparente", "Necessita analise", "Possiveis sinais"
    val authenticityNotes: String = "Padrão de impressão, tipografia e corte condizentes com tiragem autêntica.",

    // 4. PESQUISA DE PREÇOS ESPECÍFICA (ISOLADA POR IDIOMA E MERCADO)
    val averagePrice: Double = 0.0, // Preço Médio no idioma e mercado selecionado (BRL)
    val minPrice: Double = 0.0, // Menor Preço Encontrado (BRL)
    val maxPrice: Double = 0.0, // Maior Preço Encontrado (BRL)
    val marketRegion: MarketRegion = MarketRegion.BRAZIL,
    val hasReliableData: Boolean = true,
    val priceNotice: String = "",
    val offers: List<PriceOffer> = emptyList(),
    val priceHistory: List<PriceHistoryPoint> = emptyList(),
    val languageComparisons: List<LanguagePriceComparison> = emptyList(),

    // 5. METADATA E CONFIANÇA GERAL
    val confidenceScore: Int = 90, // 0 - 100
    val marketTrendComment: String = "",
    val lastUpdateDate: String = "Hoje"
) {
    val languageFlag: String get() = LanguageRegistry.getFlag(language)
    val languageDisplayName: String get() = LanguageRegistry.getDisplayName(language)
}

