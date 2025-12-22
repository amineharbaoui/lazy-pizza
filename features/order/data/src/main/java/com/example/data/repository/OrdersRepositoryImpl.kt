package com.example.data.repository

import com.example.data.datasource.OrdersRemoteDataSource
import com.example.data.mapper.OrderDomainToDtoMapper
import com.example.data.mapper.OrderDtoToDomainMapper
import com.example.domain.model.Order
import com.example.domain.repository.OrdersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OrdersRepositoryImpl @Inject constructor(
    private val ordersRemoteDataSource: OrdersRemoteDataSource,
    private val toDto: OrderDomainToDtoMapper,
    private val toDomain: OrderDtoToDomainMapper,
) : OrdersRepository {

    override suspend fun placeOrder(order: Order): Result<Unit> = ordersRemoteDataSource.createOrder(toDto.map(order))

    override fun observeMyOrders(userId: String): Flow<List<Order>> = ordersRemoteDataSource.observeOrdersByUser(userId)
        .map { list -> list.map(toDomain::map) }
}
