package com.example.order.data

import com.example.order.data.datasource.FakeOrdersRemoteDataSource
import com.example.order.data.mapper.OrderDomainToDtoMapper
import com.example.order.data.mapper.OrderDtoToDomainMapper
import com.example.order.data.repository.OrdersRepositoryImpl
import com.example.order.domain.model.Order
import com.example.order.domain.repository.OrdersRepository

class OrderTestData {

    private val remoteDataSource = FakeOrdersRemoteDataSource()
    private val toDto = OrderDomainToDtoMapper()

    val ordersRepository: OrdersRepository = OrdersRepositoryImpl(remoteDataSource, toDto, OrderDtoToDomainMapper())

    fun givenOrders(vararg orders: Order) {
        // The mapper writes every new order as IN_PROGRESS: keep the status the test asks for.
        remoteDataSource.orders.value = orders.map { toDto.map(it).copy(status = it.status.name) }
    }

    fun givenFailure(error: Exception = IllegalStateException(FORCED_FAILURE)) {
        remoteDataSource.failure = error
    }

    private companion object {
        const val FORCED_FAILURE = "Forced failure"
    }
}
