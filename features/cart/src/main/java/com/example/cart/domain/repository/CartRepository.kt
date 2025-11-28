package com.example.cart.domain.repository

import com.example.cart.domain.model.Cart
import com.example.cart.domain.model.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {

    fun observeCart(): Flow<Cart>
    suspend fun addItem(item: CartItem)
    suspend fun updateItemQuantity(
        lineId: String,
        quantity: Int,
    )

    suspend fun removeItem(lineId: String)
    suspend fun clear()

}