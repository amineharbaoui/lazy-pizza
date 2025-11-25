package com.example.menu.data.mapper

import com.example.menu.data.model.ProductDto
import com.example.menu.domain.model.MenuItem
import com.example.menu.domain.model.ProductCategory
import com.example.menu.domain.model.Topping

fun ProductDto.toMenuItem(): MenuItem {
    val categoryEnum = category.toProductCategory()
    return when (categoryEnum) {
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
        ProductCategory.TOPPING -> MenuItem.OtherMenuItem(
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