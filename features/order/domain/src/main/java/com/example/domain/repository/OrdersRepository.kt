package com.example.domain.repository

import com.example.domain.model.Order
import kotlinx.coroutines.flow.Flow

interface OrdersRepository {
    suspend fun placeOrder(order: Order): Result<Unit>
    fun observeMyOrders(userId: String): Flow<List<Order>>
}
