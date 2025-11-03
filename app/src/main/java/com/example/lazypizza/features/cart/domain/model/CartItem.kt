package com.example.lazypizza.features.cart.domain.model

data class CartItem(
    val lineId: String,
    val productId: String,
    val name: String,
    val baseUnitPrice: Double,
    val quantity: Int,
    val toppings: List<ToppingSelection>,
) {
    val unitExtras: Double get() = toppings.sumOf { it.unitPrice * it.quantity }
    val unitTotal: Double get() = baseUnitPrice + unitExtras
    val lineTotal: Double get() = unitTotal * quantity
}

data class ToppingSelection(
    val toppingId: String,
    val name: String,
    val unitPrice: Double,
    val quantity: Int,
)