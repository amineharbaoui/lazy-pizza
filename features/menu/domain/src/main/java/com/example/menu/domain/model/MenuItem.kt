package com.example.menu.domain.model

import com.example.model.ProductCategory

sealed interface MenuItem {
    val id: String
    val name: String
    val imageUrl: String
    val unitPrice: Double
    val category: ProductCategory

    data class PizzaItem(
        override val id: String,
        override val name: String,
        override val imageUrl: String,
        override val unitPrice: Double,
        override val category: ProductCategory,
        val description: String,
        val toppings: List<Topping> = emptyList(),
    ) : MenuItem

    data class OtherMenuItem(
        override val id: String,
        override val name: String,
        override val imageUrl: String,
        override val unitPrice: Double,
        override val category: ProductCategory,
    ) : MenuItem
}

data class MenuSection(
    val category: ProductCategory,
    val items: List<MenuItem>,
)

data class Topping(
    val id: String,
    val name: String,
    val unitPrice: Double,
    val imageUrl: String,
)
