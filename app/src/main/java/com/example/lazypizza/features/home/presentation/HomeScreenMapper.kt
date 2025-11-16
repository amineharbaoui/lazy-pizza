package com.example.lazypizza.features.home.presentation

import com.example.lazypizza.features.home.domain.models.CategorySection
import com.example.lazypizza.features.home.domain.models.Product
import java.text.NumberFormat
import java.util.Locale

internal fun Product.toDisplayModel(): ProductDisplayModel = ProductDisplayModel(
    id = id,
    category = category,
    name = name,
    description = description,
    price = price,
    priceFormatted = NumberFormat.getCurrencyInstance(Locale.US).format(price),
    imageUrl = imageUrl,
)

internal fun CategorySection.toDisplayModel(): CategorySectionDisplayModel = CategorySectionDisplayModel(
    category = category,
    products = products.map { it.toDisplayModel() },
)

internal fun List<CategorySection>.toDisplayModels(): List<CategorySectionDisplayModel> =
    this.map { it.toDisplayModel() }

