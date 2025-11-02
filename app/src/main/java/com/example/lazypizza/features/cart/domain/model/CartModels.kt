package com.example.lazypizza.features.cart.domain.model

/**
 * Domain models for Cart following Clean Architecture principles.
 * Money is represented as Double in this project for simplicity.
 */

data class Cart(
    val items: List<CartItem> = emptyList(),
) {
    fun itemCount(): Int = items.size
}

/**
 * Represents a selected topping for a cart item.
 *
 * @property toppingId The topping id
 * @property unitPrice The price of a single unit of this topping
 * @property quantity Selected quantity (UI limits this to [0, 3])
 */
data class CartToppingSelection(
    val toppingId: String,
    val unitPrice: Double,
    val quantity: Int,
)

/** Simple totals for the cart. Extend this later for tax/discounts if needed. */
data class CartTotals(
    val subtotal: Double,
    val total: Double = subtotal,
)