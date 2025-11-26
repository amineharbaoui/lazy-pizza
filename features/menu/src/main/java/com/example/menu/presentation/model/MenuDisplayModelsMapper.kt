package com.example.menu.presentation.model

import com.example.menu.domain.model.MenuItem
import com.example.menu.domain.model.MenuSection
import com.example.menu.domain.model.ProductCategory
import java.text.NumberFormat
import java.util.Locale

private val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale.US)

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
            formattedPrice = currencyFormat.format(price),
            description = description,
        )

        is MenuItem.OtherMenuItem -> MenuItemDisplayModel.Other(
            id = id,
            name = name,
            imageUrl = imageUrl,
            price = price,
            formattedPrice = currencyFormat.format(price),
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