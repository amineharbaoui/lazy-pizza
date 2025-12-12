package com.example.data.datasource.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val lineId: String,
    val ownerKey: String,
    val productId: String,
    val name: String,
    val imageUrl: String,
    val type: CartItemType,
    val otherItemPrice: Double?,
    val pizzaItemPrice: Double?,
    val quantity: Int,
)

enum class CartItemType {
    OTHER,
    PIZZA,
}
