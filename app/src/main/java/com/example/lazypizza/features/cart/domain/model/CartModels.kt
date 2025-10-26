package com.example.lazypizza.features.cart.domain.model

/**
 * Domain models for Cart following Clean Architecture principles.
 * Money is represented as Double in this project for simplicity.
 */

data class Cart(
    val items: List<CartItem> = emptyList()
) {
    fun itemCount(): Int = items.size
}

/**
 * One configured pizza item in the cart.
 *
 * @property id Stable identifier for this CartItem (unique within the cart)
 * @property productId The product/pizza id
 * @property basePrice Base price of the pizza
 * @property toppings Selected toppings with their unit prices and quantities
 */
data class CartItem(
    val id: String,
    val productId: String,
    val basePrice: Double,
    val toppings: List<CartToppingSelection>
)

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