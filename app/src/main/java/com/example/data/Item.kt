package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String, // "Trading Cards", "Carrinhos / Diecast", "Action Figures", "Moedas", "Outros"
    val subCategory: String = "", // "Pokémon TCG", "Magic: The Gathering", "Yu-Gi-Oh!", "One Piece Card Game", "Hot Wheels", "Matchbox", "Tomica", etc.
    val collection: String = "", // Set / Expansão / Série (e.g. "Scarlet & Violet 151", "Mainline 2024", "Commander Masters")
    val itemNumber: String = "", // e.g. "151/165", "#045/250", "OP05-060"
    val rarity: String = "Comum", // "Comum", "Incomum", "Raro", "Super Raro", "Ultra Raro", "Secret Rare", "Treasure Hunt", "Super Treasure Hunt", "Lendário"
    val variant: String = "Normal", // "Normal", "Foil / Holográfico", "Reverse Holo", "Alternate Art", "Promo", "Redline", "Edição Limitada", "Primeira Edição"
    val condition: String = "Near Mint", // "Novo / Lacrado", "Graded (Graduada)", "Mint", "Near Mint", "Lightly Played", "Moderately Played", "Heavily Played", "Damaged"
    val gradingInfo: String = "", // e.g. "PSA 10 Gem Mint", "BGS 9.5", "CGC 9"
    val language: String = "PT-BR", // "PT-BR", "EN", "JP", "ZH", "KO", "FR", "DE", "ES", "IT", "N/A"
    val scale: String = "", // e.g. "1:64", "1:18", "1:12", "N/A"
    val color: String = "", // e.g. "Azul Metálico", "Vermelho Spectraflame", "Dourado"
    val year: String = "", // e.g. "2024", "1999"
    val quantity: Int = 1,
    val purchasePrice: Double = 0.0, // Preço pago pelo usuário (em BRL)
    val estimatedValue: Double = 0.0, // Preço médio estimado atual (em BRL)
    val minPrice: Double = 0.0, // Menor preço de mercado encontrado
    val maxPrice: Double = 0.0, // Maior preço de mercado encontrado
    val confidenceScore: Int = 90, // Confiança da IA (0 a 100%)
    val imageUri: String? = null, // Foto frontal principal
    val backImageUri: String? = null, // Foto do verso / traseira
    val detailImagesJson: String = "[]", // Fotos adicionais (detalhe, defeito, embalagem, close)
    val isFavorite: Boolean = false,
    val tags: String = "",
    val storageLocation: String = "", // Local onde está guardado (ex: "Pasta Charizard - Pág 2", "Estante A", "Gaveta 1")
    val notes: String = "", // Observações do colecionador
    val priceOffersJson: String = "[]", // Lista de ofertas serializadas em JSON
    val priceHistoryJson: String = "[]", // Histórico de preços serializado em JSON
    val languageComparisonJson: String = "[]", // Comparativo de preços por idioma
    val conditionPricesJson: String = "[]", // Matriz de preços por condição (Mint, NM, Exc, Good, Played)
    val conditionAssessmentJson: String = "{}", // Checklist e detalhes da avaliação de estado
    val authenticityStatus: String = "Baixo risco aparente", // "Baixo risco aparente", "Necessita analise", "Possiveis sinais"
    val authenticityNotes: String = "", // Justificativa visual da IA
    val targetPriceAlert: Double = 0.0, // Alerta quando ultrapassar este valor
    val isAlertEnabled: Boolean = false,
    val marketRegion: String = "BR", // BR, US, JP, EU
    val cardHp: String = "", // Ex: "HP 330"
    val cardArtist: String = "", // Ex: "Mitsuhiro Arita"
    val cardAttacks: String = "", // Ex: "Brave Wing, Explosive Vortex"
    val cardSetSymbol: String = "",
    val cardOracleText: String = "", // Texto original / regras em inglês
    val cardTranslatedEffect: String = "", // O que a carta faz traduzido para Português (efeitos, habilidades, regras)
    val currency: String = "BRL",
    val lastPriceUpdate: Long = System.currentTimeMillis(),
    val dateAdded: Long = System.currentTimeMillis()
) {
    fun getOffersList(): List<PriceOffer> = JsonParserHelper.offersFromJson(priceOffersJson)
    fun getHistoryList(): List<PriceHistoryPoint> = JsonParserHelper.historyFromJson(priceHistoryJson)
    fun getLanguageComparisonList(): List<LanguagePriceComparison> = JsonParserHelper.langComparisonFromJson(languageComparisonJson)
    fun getConditionPriceTiers(): List<ConditionPriceTier> = JsonParserHelper.conditionTiersFromJson(conditionPricesJson)
    fun getConditionAssessment(): ConditionAssessment = JsonParserHelper.conditionAssessmentFromJson(conditionAssessmentJson)
    fun getDetailImages(): List<String> = JsonParserHelper.stringListFromJson(detailImagesJson)

    val totalEstimatedValue: Double get() = estimatedValue * quantity
    val totalPurchasePrice: Double get() = purchasePrice * quantity
    val profitOrLoss: Double get() = totalEstimatedValue - totalPurchasePrice
    val profitPercentage: Double
        get() = if (totalPurchasePrice > 0) {
            ((totalEstimatedValue - totalPurchasePrice) / totalPurchasePrice) * 100.0
        } else if (totalEstimatedValue > 0) {
            100.0
        } else {
            0.0
        }

    val languageFlag: String get() = LanguageRegistry.getFlag(language)
    val languageDisplayName: String get() = LanguageRegistry.getDisplayName(language)

    val isCard: Boolean
        get() = type.contains("Card", ignoreCase = true) ||
                subCategory.contains("Pokémon", ignoreCase = true) ||
                subCategory.contains("Magic", ignoreCase = true) ||
                subCategory.contains("Yu-Gi-Oh", ignoreCase = true) ||
                subCategory.contains("One Piece", ignoreCase = true) ||
                subCategory.contains("Digimon", ignoreCase = true)

    val isDiecast: Boolean
        get() = type.contains("Carrinho", ignoreCase = true) ||
                type.contains("Diecast", ignoreCase = true) ||
                subCategory.contains("Hot Wheels", ignoreCase = true) ||
                subCategory.contains("Matchbox", ignoreCase = true) ||
                subCategory.contains("Tomica", ignoreCase = true) ||
                subCategory.contains("Majorette", ignoreCase = true) ||
                subCategory.contains("Maisto", ignoreCase = true)
}

