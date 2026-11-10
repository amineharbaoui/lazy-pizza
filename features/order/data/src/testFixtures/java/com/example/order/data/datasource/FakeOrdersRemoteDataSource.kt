package com.example.order.data.datasource

import com.example.order.data.model.OrderDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

internal class FakeOrdersRemoteDataSource : OrdersRemoteDataSource {

    val orders = MutableStateFlow<List<OrderDto>>(emptyList())

    var failure: Exception? = null

    override suspend fun createOrder(order: OrderDto): Result<Unit> {
        failure?.let { return Result.failure(it) }
        orders.value += order
        return Result.success(Unit)
    }

    override fun observeOrdersByUser(userId: String): Flow<List<OrderDto>> = orders
        .onStart { failure?.let { throw it } }
        .map { all -> all.filter { it.userId == userId }.sortedByDescending { it.createdAtEpochMs } }
}
