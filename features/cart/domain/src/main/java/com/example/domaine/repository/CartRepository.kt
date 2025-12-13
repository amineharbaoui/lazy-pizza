package com.example.domaine.repository

import com.example.domaine.model.Cart
import com.example.domaine.model.CartItem
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

    suspend fun transferGuestCartToUser(userId: String)
    suspend fun clearUserCart(userId: String)
}
