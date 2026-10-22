package com.example.cart.domain.repository

import com.example.cart.domain.model.Cart
import com.example.cart.domain.model.CartItem
import com.example.cart.domain.model.CartOwner
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun observeCart(owner: CartOwner): Flow<Cart>
    suspend fun addItem(
        owner: CartOwner,
        item: CartItem,
    )

    suspend fun updateItemQuantity(
        owner: CartOwner,
        lineId: String,
        quantity: Int,
    )

    suspend fun removeItem(
        owner: CartOwner,
        lineId: String,
    )

    suspend fun clear(owner: CartOwner)

    suspend fun transferGuestCart(to: CartOwner.User)
}
