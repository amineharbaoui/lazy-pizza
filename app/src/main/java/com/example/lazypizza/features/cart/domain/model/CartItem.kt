package com.example.lazypizza.features.cart.domain.model

data class CartItem(
    val id: String,
    val productId: String,
    val basePrice: Double,
    val toppings: List<CartToppingSelection>,
)