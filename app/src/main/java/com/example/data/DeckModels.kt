package com.example.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

enum class TcgGame(val displayName: String, val defaultFormat: String) {
    POKEMON("Pokémon TCG", "Padrão (Standard)"),
    MAGIC("Magic: The Gathering", "Commander (EDH)"),
    YUGIOH("Yu-Gi-Oh!", "Avançado (Advanced)"),
    ONE_PIECE("One Piece Card Game", "Padrão")
}

data class GameFormatRule(
    val name: String,
    val minCards: Int,
    val maxCards: Int,
    val maxCopiesPerCard: Int, // e.g. 4, 1 (singleton), 3
    val hasCommanderOrLeader: Boolean,
    val description: String,
    val unlimitedBasicLandsOrEnergies: Boolean = true,
    val onlyCommons: Boolean = false // for Pauper
)

object FormatRegistry {
    val POKEMON_FORMATS = listOf(
        GameFormatRule(
            name = "Padrão (Standard)",
            minCards = 60,
            maxCards = 60,
            maxCopiesPerCard = 4,
            hasCommanderOrLeader = false,
            description = "Formato oficial competitivo. Exatamente 60 cartas, máximo 4 cópias (exceto Energias Básicas)."
        ),
        GameFormatRule(
            name = "Expandido (Expanded)",
            minCards = 60,
            maxCards = 60,
            maxCopiesPerCard = 4,
            hasCommanderOrLeader = false,
            description = "Permite expansões desde Black & White até as mais recentes. 60 cartas."
        ),
        GameFormatRule(
            name = "GLC (Gym Leader Challenge)",
            minCards = 60,
            maxCards = 60,
            maxCopiesPerCard = 1,
            hasCommanderOrLeader = false,
            description = "Formato Singleton monocolor. 60 cartas únicas, sem cartas Rule Box (sem Pokémon ex/V)."
        ),
        GameFormatRule(
            name = "Casual / Livre",
            minCards = 40,
            maxCards = 100,
            maxCopiesPerCard = 4,
            hasCommanderOrLeader = false,
            description = "Para jogar com amigos sem restrições estritas de rotação."
        )
    )

    val MAGIC_FORMATS = listOf(
        GameFormatRule(
            name = "Commander (EDH)",
            minCards = 100,
            maxCards = 100,
            maxCopiesPerCard = 1,
            hasCommanderOrLeader = true,
            description = "1 Comandante Lendário + 99 cartas únicas (Singleton). Exceto Terrenos Básicos."
        ),
        GameFormatRule(
            name = "Standard",
            minCards = 60,
            maxCards = 100,
            maxCopiesPerCard = 4,
            hasCommanderOrLeader = false,
            description = "Últimos 3 anos de coleções. Mínimo 60 cartas, máximo 4 cópias."
        ),
        GameFormatRule(
            name = "Modern",
            minCards = 60,
            maxCards = 100,
            maxCopiesPerCard = 4,
            hasCommanderOrLeader = false,
            description = "Coleções desde Mirrodin/8ª Edição até hoje. Mínimo 60 cartas."
        ),
        GameFormatRule(
            name = "Pauper",
            minCards = 60,
            maxCards = 100,
            maxCopiesPerCard = 4,
            hasCommanderOrLeader = false,
            onlyCommons = true,
            description = "Mínimo 60 cartas, exclusivamente cartas que já foram impressas como Comuns."
        ),
        GameFormatRule(
            name = "Casual",
            minCards = 60,
            maxCards = 100,
            maxCopiesPerCard = 4,
            hasCommanderOrLeader = false,
            description = "Formato de mesa livre entre amigos."
        )
    )

    val YUGIOH_FORMATS = listOf(
        GameFormatRule(
            name = "Avançado (Advanced)",
            minCards = 40,
            maxCards = 60,
            maxCopiesPerCard = 3,
            hasCommanderOrLeader = false,
            description = "Deck Principal de 40 a 60 cartas, máximo 3 cópias por card."
        ),
        GameFormatRule(
            name = "Speed Duel / Links",
            minCards = 20,
            maxCards = 30,
            maxCopiesPerCard = 3,
            hasCommanderOrLeader = false,
            description = "Partidas rápidas com 20 a 30 cartas no deck."
        )
    )

    val ONE_PIECE_FORMATS = listOf(
        GameFormatRule(
            name = "Padrão",
            minCards = 51, // 1 Líder + 50 deck
            maxCards = 51,
            maxCopiesPerCard = 4,
            hasCommanderOrLeader = true,
            description = "1 Carta de Líder + 50 cartas de deck + 10 cartas DON!! (máximo 4 cópias)."
        ),
        GameFormatRule(
            name = "Casual",
            minCards = 30,
            maxCards = 60,
            maxCopiesPerCard = 4,
            hasCommanderOrLeader = true,
            description = "Partidas casuais e decks em construção."
        )
    )

    fun getFormatsForGame(game: String): List<GameFormatRule> {
        return when {
            game.contains("Pokémon", ignoreCase = true) -> POKEMON_FORMATS
            game.contains("Magic", ignoreCase = true) -> MAGIC_FORMATS
            game.contains("Yu-Gi-Oh", ignoreCase = true) -> YUGIOH_FORMATS
            game.contains("One Piece", ignoreCase = true) -> ONE_PIECE_FORMATS
            else -> POKEMON_FORMATS
        }
    }

    fun getRule(game: String, formatName: String): GameFormatRule {
        val formats = getFormatsForGame(game)
        return formats.firstOrNull { it.name.equals(formatName, ignoreCase = true) }
            ?: formats.first()
    }
}

@Entity(tableName = "decks")
data class Deck(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val game: String = "Pokémon TCG",
    val format: String = "Padrão (Standard)",
    val commanderOrLeader: String = "",
    val description: String = "",
    val coverImageUrl: String = "",
    val themeColorHex: String = "#3B82F6",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "deck_cards",
    indices = [Index(value = ["deckId"])],
    foreignKeys = [
        ForeignKey(
            entity = Deck::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DeckCard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val deckId: Int,
    val name: String,
    val cardType: String = "Pokémon", // Pokémon, Treinador, Energia / Criatura, Mágica, Terreno / etc.
    val subType: String = "", // e.g. "Apoiador", "Item", "Instant", "Sorcery"
    val collection: String = "",
    val itemNumber: String = "",
    val imageUrl: String = "",
    val quantity: Int = 1,
    val estimatedPriceBrl: Double = 0.0,
    val manaCostOrHp: String = "", // e.g. "HP 330", "{2}{R}{R}", "Nível 7"
    val rarity: String = "Comum",
    val isCommanderOrLeader: Boolean = false,
    val isBasicEnergyOrLand: Boolean = false,
    val notes: String = ""
)

data class DeckWithCards(
    @Embedded val deck: Deck,
    @Relation(
        parentColumn = "id",
        entityColumn = "deckId"
    )
    val cards: List<DeckCard>
)

data class DeckInventoryAnalysis(
    val totalCardsCount: Int,
    val targetCountText: String,
    val isCountValid: Boolean,
    val countWarning: String?,
    val isLegalityValid: Boolean,
    val legalityIssues: List<String>,
    val ownedCardsCount: Int,
    val missingCardsCount: Int,
    val completionPercentage: Int,
    val totalDeckMarketValue: Double,
    val ownedMarketValue: Double,
    val missingEstimatedCost: Double,
    val cardTypeDistribution: Map<String, Int>,
    val missingCards: List<DeckCard>
)
