package com.example.data.datasource.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_toppings")
data class CartToppingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val lineId: String,
    val toppingId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
)
