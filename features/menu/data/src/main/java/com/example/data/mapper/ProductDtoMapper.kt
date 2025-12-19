package com.example.data.mapper

import com.example.data.model.ProductDto
import com.example.domain.model.MenuItem
import com.example.domain.model.Topping
import com.example.model.ProductCategory

fun ProductDto.toMenuItem(): MenuItem = when (val categoryEnum = category.toProductCategory()) {
    ProductCategory.PIZZA -> MenuItem.PizzaItem(
        id = id,
        name = name,
        imageUrl = imageUrl,
        unitPrice = price,
        description = description,
        toppings = emptyList(),
        category = categoryEnum,
    )

    ProductCategory.DRINK,
    ProductCategory.SAUCE,
    ProductCategory.ICE_CREAM,
    ProductCategory.TOPPING,
    -> MenuItem.OtherMenuItem(
        id = id,
        name = name,
        imageUrl = imageUrl,
        unitPrice = price,
        category = categoryEnum,
    )
}

fun ProductDto.toTopping(): Topping {
    require(category.toProductCategory() == ProductCategory.TOPPING) {
        "ProductDto must have category TOPPING to map to Topping"
    }
    return Topping(
        id = id,
        name = name,
        unitPrice = price,
        imageUrl = imageUrl,
    )
}

fun String.toProductCategory(): ProductCategory = ProductCategory.entries.firstOrNull { it.name == this }
    ?: throw IllegalArgumentException("Invalid product category: $this")
