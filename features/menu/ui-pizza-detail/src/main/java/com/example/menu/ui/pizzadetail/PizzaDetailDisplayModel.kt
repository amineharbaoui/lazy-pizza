package com.example.menu.ui.pizzadetail

data class PizzaDetailDisplayModel(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val unitPrice: Double,
    val unitPriceFormatted: String,
)

data class ToppingDisplayModel(
    val id: String,
    val name: String,
    val unitPrice: Double,
    val unitPriceFormatted: String,
    val imageUrl: String,
)
