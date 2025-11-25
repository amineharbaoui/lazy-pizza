package com.example.lazypizza.features.home.domain.models

import com.example.menu.domain.model.ProductCategory

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val basePrice: Double,
    val imageUrl: String,
    val category: ProductCategory,
)

data class CategorySection(
    val category: ProductCategory,
    val products: List<Product>,
)