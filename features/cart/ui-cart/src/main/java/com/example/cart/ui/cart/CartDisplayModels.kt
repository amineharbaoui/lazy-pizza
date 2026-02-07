package com.example.cart.ui.cart

import com.example.core.model.ProductCategory

data class CartLineDisplayModel(
    val lineId: String,
    val productId: String,
    val name: String,
    val subtitleLines: List<String>,
    val imageUrl: String,
    val quantity: Int,
    val unitPriceFormatted: String,
    val lineTotalFormatted: String,
)

data class CartDisplayModel(
    val items: List<CartLineDisplayModel>,
    val totalPriceFormatted: String,
)

data class RecommendedItemDisplayModel(
    val id: String,
    val name: String,
    val unitPrice: Double,
    val unitPriceFormatted: String,
    val imageUrl: String,
    val category: ProductCategory,
)
