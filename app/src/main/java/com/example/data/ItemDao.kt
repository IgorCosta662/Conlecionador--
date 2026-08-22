package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items ORDER BY dateAdded DESC")
    fun getAllItems(): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE id = :id LIMIT 1")
    suspend fun getItemById(id: Int): Item?

    @Query("SELECT * FROM items WHERE isFavorite = 1 ORDER BY dateAdded DESC")
    fun getFavoriteItems(): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE type = :type ORDER BY dateAdded DESC")
    fun getItemsByType(type: String): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE name LIKE '%' || :query || '%' OR collection LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%'")
    fun searchItems(query: String): Flow<List<Item>>

    @Query("SELECT COUNT(*) FROM items")
    fun getTotalItemsCount(): Flow<Int>

    @Query("SELECT * FROM items WHERE id IN (:ids)")
    suspend fun getItemsByIds(ids: List<Int>): List<Item>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: Item): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<Item>)

    @Update
    suspend fun updateItem(item: Item)

    @Delete
    suspend fun deleteItem(item: Item)

    @Query("DELETE FROM items WHERE id = :id")
    suspend fun deleteItemById(id: Int)

    @Query("DELETE FROM items")
    suspend fun deleteAllItems()
}
