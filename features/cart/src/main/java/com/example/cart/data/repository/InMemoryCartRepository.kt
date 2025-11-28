package com.example.cart.data.repository

import com.example.cart.domain.model.Cart
import com.example.cart.domain.model.CartItem
import com.example.cart.domain.model.CartTopping
import com.example.cart.domain.model.PizzaCartItem
import com.example.cart.domain.model.SimpleCartItem
import com.example.cart.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryCartRepository @Inject constructor() : CartRepository {

    private val cartState = MutableStateFlow(Cart())

    override fun observeCart(): Flow<Cart> = cartState.asStateFlow()

    override suspend fun addItem(item: CartItem) {
        val current = cartState.value

        val newItems = when (item) {
            is SimpleCartItem -> addOrMergeSimple(current, item)
            is PizzaCartItem -> addOrMergePizza(current, item)
        }

        cartState.value = Cart(newItems)
    }

    override suspend fun updateItemQuantity(
        lineId: String,
        quantity: Int,
    ) {
        val current = cartState.value
        val newItems = current.items.mapNotNull { line ->
            if (line.lineId != lineId) return@mapNotNull line

            if (quantity <= 0) {
                // remove this item
                null
            } else {
                when (line) {
                    is SimpleCartItem -> line.copy(quantity = quantity)
                    is PizzaCartItem -> line.copy(quantity = quantity)
                }
            }
        }
        cartState.value = Cart(newItems)
    }

    override suspend fun removeItem(lineId: String) {
        val current = cartState.value
        cartState.value = Cart(current.items.filterNot { it.lineId == lineId })
    }

    override suspend fun clear() {
        cartState.value = Cart(emptyList())
    }

    // --- merge helpers -----------------------------------------------------

    private fun addOrMergeSimple(
        cart: Cart,
        newItem: SimpleCartItem,
    ): List<CartItem> {
        val existingIndex = cart.items.indexOfFirst {
            it is SimpleCartItem && it.productId == newItem.productId
        }

        if (existingIndex == -1) return cart.items + newItem

        val updated = (cart.items[existingIndex] as SimpleCartItem).copy(
            quantity = cart.items[existingIndex].quantity + newItem.quantity
        )

        return cart.items.toMutableList().apply {
            this[existingIndex] = updated
        }
    }

    /**
     * For pizzas we only merge if base pizza + toppings *match*.
     * Otherwise we keep separate lines.
     */
    private fun addOrMergePizza(
        cart: Cart,
        newItem: PizzaCartItem,
    ): List<CartItem> {
        val existingIndex = cart.items.indexOfFirst {
            it is PizzaCartItem && it.productId == newItem.productId &&
                    it.basePrice == newItem.basePrice &&
                    it.toppings.sameConfigurationAs(newItem.toppings)
        }

        if (existingIndex == -1) return cart.items + newItem

        val updated = (cart.items[existingIndex] as PizzaCartItem).copy(
            quantity = cart.items[existingIndex].quantity + newItem.quantity
        )

        return cart.items.toMutableList().apply {
            this[existingIndex] = updated
        }
    }

}

private fun List<CartTopping>.sameConfigurationAs(other: List<CartTopping>): Boolean {
    if (size != other.size) return false
    val thisMap = associateBy({ it.toppingId }) { it.quantity }
    val otherMap = other.associateBy({ it.toppingId }) { it.quantity }
    return thisMap == otherMap
}