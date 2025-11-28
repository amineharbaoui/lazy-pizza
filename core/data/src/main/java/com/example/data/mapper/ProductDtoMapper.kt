package com.example.data.mapper

import com.example.data.model.ProductDto
import com.example.domain.model.MenuItem
import com.example.domain.model.ProductCategory
import com.example.domain.model.Topping

fun ProductDto.toMenuItem(): MenuItem {
    return when (val categoryEnum = category.toProductCategory()) {
        ProductCategory.PIZZA -> MenuItem.PizzaItem(
            id = id,
            name = name,
            imageUrl = imageUrl,
            price = price,
            description = description,
            toppings = emptyList(),
        )

        ProductCategory.DRINK,
        ProductCategory.SAUCE,
        ProductCategory.ICE_CREAM,
        ProductCategory.TOPPING,
            -> MenuItem.OtherMenuItem(
            id = id,
            name = name,
            imageUrl = imageUrl,
            price = price,
            category = categoryEnum,
        )
    }
}

fun ProductDto.toTopping(): Topping {
    require(category.toProductCategory() == ProductCategory.TOPPING) {
        "ProductDto must have category TOPPING to map to Topping"
    }
    return Topping(
        id = id,
        name = name,
        price = price,
        imageUrl = imageUrl,
    )
}

fun String.toProductCategory(): ProductCategory {
    return ProductCategory.entries.firstOrNull { it.name == this }
        ?: throw IllegalArgumentException("Invalid product category: $this")
}