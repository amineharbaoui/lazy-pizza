package com.example.uilogin.pizzadetail

import com.example.model.ProductCategory
import com.example.uilogin.utils.formatting.toFormattedCurrency

data class MenuSectionDisplayModel(
    val category: ProductCategory,
    val items: List<MenuItemDisplayModel>,
)

sealed interface MenuItemDisplayModel {
    val id: String
    val name: String
    val imageUrl: String
    val unitPrice: Double
    val formattedUnitPrice: String
    val category: ProductCategory

    data class Pizza(
        override val id: String,
        override val name: String,
        override val imageUrl: String,
        override val unitPrice: Double,
        override val formattedUnitPrice: String,
        override val category: ProductCategory,
        val description: String,
    ) : MenuItemDisplayModel

    data class Other(
        override val id: String,
        override val name: String,
        override val imageUrl: String,
        override val unitPrice: Double,
        override val formattedUnitPrice: String,
        override val category: ProductCategory,
        val quantity: Int,
    ) : MenuItemDisplayModel {
        val totalPriceFormatted: String get() = (unitPrice * quantity).toFormattedCurrency()
        val inCart: Boolean get() = quantity > 0
    }
}

enum class MenuTag(val displayName: String) {
    PIZZA("Pizza"),
    DRINK("Drink"),
    SAUCE("Sauce"),
    ICE_CREAM("Ice cream"),
}
