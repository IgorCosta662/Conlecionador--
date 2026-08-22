package com.example.data

import kotlinx.coroutines.flow.Flow

class ItemRepository(private val itemDao: ItemDao) {
    val allItems: Flow<List<Item>> = itemDao.getAllItems()
    val favoriteItems: Flow<List<Item>> = itemDao.getFavoriteItems()
    val totalCount: Flow<Int> = itemDao.getTotalItemsCount()

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
