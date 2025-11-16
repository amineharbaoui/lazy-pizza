package com.example.lazypizza.features.home.data.mapper

import com.example.lazypizza.features.home.domain.models.CategorySection
import com.example.lazypizza.features.home.domain.models.Product

fun List<Product>.toSections(): List<CategorySection> =
    groupBy { it.category }
        .map { (category, products) -> CategorySection(category = category, products = products) }
        .sortedBy { it.category.ordinal }
