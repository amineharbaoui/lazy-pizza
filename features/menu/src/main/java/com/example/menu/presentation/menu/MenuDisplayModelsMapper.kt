package com.example.menu.presentation.menu

import com.example.domain.model.MenuItem
import com.example.domain.model.MenuSection
import com.example.domain.model.ProductCategory
import com.example.ui.utils.formatting.toFormattedCurrency

fun MenuSection.toDisplayModel(): MenuSectionDisplayModel =
    MenuSectionDisplayModel(
        category = category,
        items = items.map { it.toDisplayModel() }
    )

fun MenuItem.toDisplayModel(): MenuItemDisplayModel =
    when (this) {
        is MenuItem.PizzaItem -> MenuItemDisplayModel.Pizza(
            id = id,
            name = name,
            imageUrl = imageUrl,
            price = price,
            formattedPrice = price.toFormattedCurrency(),
            description = description,
        )

        is MenuItem.OtherMenuItem -> MenuItemDisplayModel.Other(
            id = id,
            name = name,
            imageUrl = imageUrl,
            price = price,
            formattedPrice = price.toFormattedCurrency(),
            category = category,
        )
    }

fun ProductCategory.toMenuTag(): MenuTag? = when (this) {
    ProductCategory.PIZZA -> MenuTag.PIZZA
    ProductCategory.DRINK -> MenuTag.DRINK
    ProductCategory.SAUCE -> MenuTag.SAUCE
    ProductCategory.ICE_CREAM -> MenuTag.ICE_CREAM
    ProductCategory.TOPPING -> null
}