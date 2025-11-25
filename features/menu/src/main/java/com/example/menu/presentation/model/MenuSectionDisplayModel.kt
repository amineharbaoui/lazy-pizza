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
    val priceText: String

    data class Pizza(
        override val id: String,
        override val name: String,
        override val imageUrl: String,
        override val priceText: String,
        val description: String,
    ) : MenuItemDisplayModel

    data class Other(
        override val id: String,
        override val name: String,
        override val imageUrl: String,
        override val priceText: String,
        val category: ProductCategory,
    ) : MenuItemDisplayModel
}