package com.example.order.data.datasource

import com.example.order.data.model.OrderDto
import kotlinx.coroutines.flow.Flow

interface OrdersRemoteDataSource {
    suspend fun createOrder(order: OrderDto): Result<Unit>
    fun observeOrdersByUser(userId: String): Flow<List<OrderDto>>
}
