package com.example.lazypizza.features.cart.domain.repository

import com.example.lazypizza.features.cart.domain.model.Cart
import com.example.lazypizza.features.cart.domain.model.CartItem
import kotlinx.coroutines.flow.StateFlow

/**
 * Abstraction for accessing and mutating the Cart aggregate.
 */
interface CartRepository {
    /** Continuous stream of the current cart. */
    val cart: StateFlow<Cart>

    /** Replace current cart with the provided one. */
    suspend fun setCart(cart: Cart)

    /** Add a new configured item to the cart. */
    suspend fun addItem(item: CartItem)

    /** Remove an item by its cart item id. */
    suspend fun removeItem(itemId: String)

    /** Update (or remove if quantity <= 0) a topping quantity for an item. */
    suspend fun updateItemToppingQuantity(itemId: String, toppingId: String, quantity: Int)

    /** Remove all items from the cart. */
    suspend fun clear()
}