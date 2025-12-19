package com.example.data.repository

import com.example.data.datasource.OrdersRemoteDataSource
import com.example.data.mapper.toDto
import com.example.domain.model.Order
import com.example.domain.repository.OrdersRepository
import javax.inject.Inject

class OrdersRepositoryImpl @Inject constructor(
    private val ordersRemoteDataSource: OrdersRemoteDataSource,
) : OrdersRepository {

    override suspend fun placeOrder(order: Order): Result<Unit> = ordersRemoteDataSource.createOrder(order.toDto())
}
