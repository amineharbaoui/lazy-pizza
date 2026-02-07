package com.example.order.domain.repository

import com.example.order.domain.model.Order
import kotlinx.coroutines.flow.Flow

interface OrdersRepository {
    suspend fun placeOrder(order: Order): Result<Unit>
    fun observeMyOrders(userId: String): Flow<List<Order>>
}
