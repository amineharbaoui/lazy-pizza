package com.example.lazypizza.features.home.domain

data class Product(
    val id: String,
    val category: ProductCategory,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String
)

data class CategorySection(
    val category: ProductCategory,
    val products: List<Product>
)