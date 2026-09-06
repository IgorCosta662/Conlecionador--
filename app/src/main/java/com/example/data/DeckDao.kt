package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Query("SELECT * FROM decks ORDER BY updatedAt DESC")
    fun getAllDecks(): Flow<List<Deck>>

    @Transaction
    @Query("SELECT * FROM decks WHERE id = :deckId LIMIT 1")
    fun getDeckWithCards(deckId: Int): Flow<DeckWithCards?>

    @Transaction
    @Query("SELECT * FROM decks ORDER BY updatedAt DESC")
    fun getAllDecksWithCards(): Flow<List<DeckWithCards>>

    @Query("SELECT * FROM deck_cards WHERE deckId = :deckId")
    fun getCardsForDeck(deckId: Int): Flow<List<DeckCard>>

    @Query("SELECT * FROM decks WHERE id = :id LIMIT 1")
    suspend fun getDeckById(id: Int): Deck?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: Deck): Long

    @Update
    suspend fun updateDeck(deck: Deck)

    @Delete
    suspend fun deleteDeck(deck: Deck)

    @Query("DELETE FROM decks WHERE id = :id")
    suspend fun deleteDeckById(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeckCard(card: DeckCard): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeckCards(cards: List<DeckCard>)

    @Update
    suspend fun updateDeckCard(card: DeckCard)

    @Delete
    suspend fun deleteDeckCard(card: DeckCard)

    @Query("DELETE FROM deck_cards WHERE id = :id")
    suspend fun deleteDeckCardById(id: Int)

    @Query("DELETE FROM deck_cards WHERE deckId = :deckId")
    suspend fun clearCardsForDeck(deckId: Int)
}
