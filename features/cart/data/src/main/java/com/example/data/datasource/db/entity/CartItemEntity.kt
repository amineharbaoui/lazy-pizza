package com.example.data.datasource.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.ProductCategory

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val lineId: String,
    val ownerKey: String,
    val productId: String,
    val name: String,
    val imageUrl: String,
    val category: ProductCategory,
    val otherItemPrice: Double?,
    val pizzaItemPrice: Double?,
    val quantity: Int,
)
