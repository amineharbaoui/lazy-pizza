package com.example.uilogin.pizzadetail

data class PizzaDetailDisplayModel(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val price: Double,
    val priceFormatted: String,
)

data class ToppingDisplayModel(
    val id: String,
    val name: String,
    val price: Double,
    val priceFormatted: String,
    val imageUrl: String,
)
