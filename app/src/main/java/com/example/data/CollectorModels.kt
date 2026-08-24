package com.example.data

import com.squareup.moshi.JsonClass

// ----------------------------------------------------
// App Modes: Colecionador vs Investidor
// ----------------------------------------------------
enum class AppMode(
    val title: String,
    val subtitle: String,
    val description: String
) {
    COLLECTOR(
        "Modo Colecionador",
        "Foco em Completar Sets & Organização",
        "Checklists de coleções, o que falta, inventário físico de pastas/caixas e duplicatas para troca."
    ),
    INVESTOR(
        "Modo Investidor",
        "Foco em Valorização & Patrimônio",
        "Patrimônio total, lucro/prejuízo, maiores altas, histórico de preços (ATH), alertas e oportunidades."
    )
}

// ----------------------------------------------------
// Physical Storage Location Details
// ----------------------------------------------------
@JsonClass(generateAdapter = true)
data class PhysicalLocation(
    val containerType: String = "Pasta / Binder", // "Pasta / Binder", "Caixa / Box", "Estante / Vitrine", "Gaveta", "Cofre"
    val containerName: String = "Pasta Principal", // Ex: "Binder Ultra Pro 01", "Caixa Hot Wheels 2"
    val pageOrShelf: String = "1", // Página 1, Estante 3
    val slotOrPosition: String = "1", // Slot 4, Posição B7
    val notes: String = ""
) {
    fun toDisplayString(): String {
        return when {
            containerName.isBlank() -> "Não especificado"
            pageOrShelf.isNotBlank() && slotOrPosition.isNotBlank() -> "$containerName (Pág/Prat: $pageOrShelf, Slot: $slotOrPosition)"
            pageOrShelf.isNotBlank() -> "$containerName (Pág: $pageOrShelf)"
            else -> containerName
        }
    }
}

// ----------------------------------------------------
// Condition Breakdown (e.g. 2 Near Mint, 1 Played, 1 Damaged)
// ----------------------------------------------------
@JsonClass(generateAdapter = true)
data class ConditionCount(
    val condition: String, // "Near Mint", "Played", "Damaged", "Novo / Lacrado", "Graded (PSA/BGS)"
    val count: Int,
    val individualPurchasePrice: Double = 0.0,
    val notes: String = ""
)

// ----------------------------------------------------
// Price Alert Model
// ----------------------------------------------------
@JsonClass(generateAdapter = true)
data class PriceAlert(
    val id: String = java.util.UUID.randomUUID().toString(),
    val itemId: Int? = null,
    val itemName: String,
    val itemCategory: String,
    val targetMinPrice: Double = 0.0, // Avisar se baixar deste valor
    val targetMaxPrice: Double = 0.0, // Avisar se passar deste valor
    val currentPrice: Double = 0.0,
    val isEnabled: Boolean = true,
    val isTriggered: Boolean = false,
    val triggerMessage: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

// ----------------------------------------------------
// Set / Checklist Definition
// ----------------------------------------------------
data class CollectibleSet(
    val id: String,
    val name: String,
    val franchise: String, // "Pokémon TCG", "Magic: The Gathering", "Yu-Gi-Oh!", "Hot Wheels"
    val year: String,
    val totalItems: Int,
    val iconCategory: String,
    val bannerColor: Long,
    val description: String,
    val items: List<SetCardItem>
)

data class SetCardItem(
    val number: String,
    val name: String,
    val rarity: String,
    val variant: String = "Normal",
    val estimatedPriceBrl: Double,
    val language: String = "PT-BR"
)

// ----------------------------------------------------
// Set Progress Summary
// ----------------------------------------------------
data class SetProgressInfo(
    val set: CollectibleSet,
    val ownedCount: Int,
    val totalCount: Int,
    val missingCount: Int,
    val completionPercentage: Double,
    val estimatedCostToCompleteBrl: Double,
    val ownedItems: List<Item>,
    val missingItems: List<SetCardItem>
)

// ----------------------------------------------------
// AI Assistant Message
// ----------------------------------------------------
data class AiChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "user" or "assistant"
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val relatedItemIds: List<Int> = emptyList(),
    val isActionable: Boolean = false,
    val actionRoute: String? = null
)

