package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wishlist_items")
data class WishlistItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String, // "Trading Cards", "Carrinhos / Diecast", "Action Figures", "Moedas", "Outros"
    val subCategory: String = "", // "Pokémon TCG", "Hot Wheels", etc.
    val targetMaxPrice: Double = 0.0, // Preço alvo máximo a pagar
    val priority: String = "Alta", // "Máxima (Santo Graal)", "Alta", "Média", "Baixa"
    val notes: String = "",
    val isFound: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis()
)
