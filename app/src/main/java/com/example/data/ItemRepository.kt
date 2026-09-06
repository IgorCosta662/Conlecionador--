package com.example.data

import kotlinx.coroutines.flow.Flow

class ItemRepository(
    private val itemDao: ItemDao,
    private val wishlistDao: WishlistDao? = null,
    private val deckDao: DeckDao? = null
) {
    val allItems: Flow<List<Item>> = itemDao.getAllItems()
    val favoriteItems: Flow<List<Item>> = itemDao.getFavoriteItems()
    val totalCount: Flow<Int> = itemDao.getTotalItemsCount()
    val allWishlist: Flow<List<WishlistItem>> = wishlistDao?.getAllWishlistItems() ?: kotlinx.coroutines.flow.flowOf(emptyList())
    val allDecksWithCards: Flow<List<DeckWithCards>> = deckDao?.getAllDecksWithCards() ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun getDeckWithCards(deckId: Int): Flow<DeckWithCards?> {
        return deckDao?.getDeckWithCards(deckId) ?: kotlinx.coroutines.flow.flowOf(null)
    }

    suspend fun insertDeck(deck: Deck): Long = deckDao?.insertDeck(deck) ?: -1L
    suspend fun updateDeck(deck: Deck) = deckDao?.updateDeck(deck)
    suspend fun deleteDeckById(deckId: Int) = deckDao?.deleteDeckById(deckId)

    suspend fun insertDeckCard(card: DeckCard): Long = deckDao?.insertDeckCard(card) ?: -1L
    suspend fun insertDeckCards(cards: List<DeckCard>) = deckDao?.insertDeckCards(cards)
    suspend fun updateDeckCard(card: DeckCard) = deckDao?.updateDeckCard(card)
    suspend fun deleteDeckCard(card: DeckCard) = deckDao?.deleteDeckCard(card)
    suspend fun deleteDeckCardById(cardId: Int) = deckDao?.deleteDeckCardById(cardId)
    suspend fun clearCardsForDeck(deckId: Int) = deckDao?.clearCardsForDeck(deckId)

    suspend fun insertWishlist(item: WishlistItem): Long {
        return wishlistDao?.insert(item) ?: -1L
    }

    suspend fun updateWishlist(item: WishlistItem) {
        wishlistDao?.update(item)
    }

    suspend fun deleteWishlist(item: WishlistItem) {
        wishlistDao?.delete(item)
    }

    suspend fun getItemById(id: Int): Item? {
        return itemDao.getItemById(id)
    }

    suspend fun getItemsByIds(ids: List<Int>): List<Item> {
        return itemDao.getItemsByIds(ids)
    }

    suspend fun insertItem(item: Item): Long {
        return itemDao.insertItem(item)
    }

    suspend fun insertItems(items: List<Item>) {
        itemDao.insertItems(items)
    }

    suspend fun updateItem(item: Item) {
        itemDao.updateItem(item)
    }

    suspend fun deleteItem(item: Item) {
        itemDao.deleteItem(item)
    }

    suspend fun deleteItemById(id: Int) {
        itemDao.deleteItemById(id)
    }

    suspend fun deleteAllItems() {
        itemDao.deleteAllItems()
    }
}
