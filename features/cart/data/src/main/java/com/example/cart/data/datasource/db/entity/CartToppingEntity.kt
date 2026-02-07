package com.example.cart.data.datasource.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_toppings")
data class CartToppingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ownerKey: String,
    val lineId: String,
    val toppingId: String,
    val name: String,
    val unitPrice: Double,
    val quantity: Int,
)
