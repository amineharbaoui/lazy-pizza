package com.example.data.datasource.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class CartLineWithToppings(
    @Embedded val item: CartItemEntity,
    @Relation(
        parentColumn = "lineId",
        entityColumn = "lineId",
    )
    val toppings: List<CartToppingEntity>,
)
