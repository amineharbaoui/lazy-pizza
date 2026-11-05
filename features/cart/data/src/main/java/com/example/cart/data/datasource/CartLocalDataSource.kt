package com.example.cart.data.datasource

import com.example.cart.domain.model.Cart
import com.example.cart.domain.model.CartItem
import kotlinx.coroutines.flow.Flow

interface CartLocalDataSource {
    fun observeCart(ownerKey: String): Flow<Cart>
    suspend fun addItem(
        ownerKey: String,
        item: CartItem,
    )

    /** Writes every line in one transaction, or none. */
    suspend fun addItems(
        ownerKey: String,
        items: List<CartItem>,
    )

    suspend fun updateItemQuantity(
        ownerKey: String,
        lineId: String,
        quantity: Int,
    )

    suspend fun removeItem(
        ownerKey: String,
        lineId: String,
    )

    suspend fun clear(ownerKey: String)
}
