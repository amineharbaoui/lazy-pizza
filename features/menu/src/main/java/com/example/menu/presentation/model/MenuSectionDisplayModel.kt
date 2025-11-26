package com.example.menu.presentation.model

import com.example.menu.domain.model.ProductCategory

data class MenuSectionDisplayModel(
    val category: ProductCategory,
    val items: List<MenuItemDisplayModel>,
)

sealed interface MenuItemDisplayModel {
    val id: String
    val name: String
    val imageUrl: String
    val price: Double
    val formattedPrice: String

    data class Pizza(
        override val id: String,
        override val name: String,
        override val imageUrl: String,
        override val price: Double,
        override val formattedPrice: String,
        val description: String,
    ) : MenuItemDisplayModel

    data class Other(
        override val id: String,
        override val name: String,
        override val imageUrl: String,
        override val price: Double,
        override val formattedPrice: String,
        val category: ProductCategory,
    ) : MenuItemDisplayModel
}

enum class MenuTag(val displayName: String) {
    PIZZA("Pizza"),
    DRINK("Drink"),
    SAUCE("Sauce"),
    ICE_CREAM("Ice cream"),
}