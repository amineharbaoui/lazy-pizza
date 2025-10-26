package com.example.lazypizza.features.cart.data.repository

import com.example.lazypizza.features.cart.domain.model.Cart
import com.example.lazypizza.features.cart.domain.model.CartItem
import com.example.lazypizza.features.cart.domain.model.CartToppingSelection
import com.example.lazypizza.features.cart.domain.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

/**
 * Simple in-memory CartRepository implementation.
 * This keeps cart state in a MutableStateFlow and is lost when the app process dies.
 */
class InMemoryCartRepository @Inject constructor() : CartRepository {

    private val mutex = Mutex()
    private val _cart = MutableStateFlow(Cart())

    override val cart: StateFlow<Cart> = _cart

    override suspend fun setCart(cart: Cart) {
        mutex.withLock {
            _cart.value = cart
        }
    }

    override suspend fun addItem(item: CartItem) {
        mutex.withLock {
            val current = _cart.value
            _cart.value = current.copy(items = current.items + item)
        }
    }

    override suspend fun removeItem(itemId: String) {
        mutex.withLock {
            val current = _cart.value
            _cart.value = current.copy(items = current.items.filterNot { it.id == itemId })
        }
    }

    override suspend fun updateItemToppingQuantity(itemId: String, toppingId: String, quantity: Int) {
        mutex.withLock {
            val current = _cart.value
            val updatedItems = current.items.map { item ->
                if (item.id != itemId) return@map item
                val existing = item.toppings.find { it.toppingId == toppingId }
                val newToppings: List<CartToppingSelection> = when {
                    existing == null -> {
                        // If topping isn't part of the item, we ignore additions here to avoid missing unitPrice.
                        // Use AddItemToCart with the desired toppings to add new ones.
                        item.toppings
                    }
                    quantity <= 0 -> item.toppings.filterNot { it.toppingId == toppingId }
                    else -> item.toppings.map {
                        if (it.toppingId == toppingId) it.copy(quantity = quantity) else it
                    }
                }
                item.copy(toppings = newToppings)
            }
            _cart.value = current.copy(items = updatedItems)
        }
    }

    override suspend fun clear() {
        mutex.withLock { _cart.value = Cart() }
    }
}