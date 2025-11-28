package com.example.cart.domain.model

import java.util.UUID

data class CartTopping(
    val toppingId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
)

sealed interface CartItem {
    val lineId: String
    val productId: String
    val name: String
    val imageUrl: String
    val quantity: Int
    val lineTotal: Double
}

data class SimpleCartItem(
    override val lineId: String = UUID.randomUUID().toString(),
    override val productId: String,
    override val name: String,
    override val imageUrl: String,
    val unitPrice: Double,
    override val quantity: Int,
) : CartItem {
    override val lineTotal: Double
        get() = unitPrice * quantity
}

data class PizzaCartItem(
    override val lineId: String = UUID.randomUUID().toString(),
    override val productId: String,
    override val name: String,
    override val imageUrl: String,
    val basePrice: Double,
    val toppings: List<CartTopping>,
    override val quantity: Int,
) : CartItem {

    private val toppingsPriceForOne: Double
        get() = toppings.sumOf { it.price * it.quantity }

    override val lineTotal: Double
        get() = (basePrice + toppingsPriceForOne) * quantity
}

data class Cart(
    val items: List<CartItem> = emptyList(),
) {
    val subtotal: Double
        get() = items.sumOf { it.lineTotal }
}