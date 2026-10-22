package com.example.order.data.datasource

import com.example.order.data.model.OrderDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeOrdersRemoteDataSource : OrdersRemoteDataSource {

    private val ordersByUser = MutableStateFlow<Map<String, List<OrderDto>>>(emptyMap())

    fun setOrders(
        userId: String,
        orders: List<OrderDto>,
    ) {
        ordersByUser.update { it + (userId to orders) }
    }

    override suspend fun createOrder(order: OrderDto): Result<Unit> {
        ordersByUser.update { it + (order.userId to it[order.userId].orEmpty() + order) }
        return Result.success(Unit)
    }

    override fun observeOrdersByUser(userId: String): Flow<List<OrderDto>> = ordersByUser.map { it[userId].orEmpty() }
}
