package com.example.lazypizza.features.home.data.mapper

import com.example.lazypizza.features.home.data.models.ProductDTO
import com.example.lazypizza.features.home.domain.models.CategorySection
import com.example.lazypizza.features.home.domain.models.Product
import com.example.lazypizza.features.home.domain.models.ProductCategory

fun ProductDTO.toDomain(): Product {
    val cat = runCatching { ProductCategory.valueOf(category) }.getOrElse { ProductCategory.PIZZA }
    return Product(
        id = id,
        name = name,
        description = description,
        price = price,
        category = cat,
        imageUrl = imageUrl
    )
}

fun List<Product>.toSections(): List<CategorySection> =
    groupBy { it.category }
        .map { (category, products) -> CategorySection(category = category, products = products) }
        .sortedBy { it.category.ordinal }
