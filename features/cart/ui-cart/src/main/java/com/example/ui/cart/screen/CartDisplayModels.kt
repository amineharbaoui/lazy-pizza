package com.example.ui.cart.screen

data class CartLineDisplayModel(
    val lineId: String,
    val title: String,
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
    val title: String,
    val price: Double,
    val priceFormatted: String,
    val imageUrl: String,
)
