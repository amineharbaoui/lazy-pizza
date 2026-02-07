package com.example.order.domain.usecase

import com.example.order.domain.model.Order
import com.example.order.domain.repository.OrdersRepository
import javax.inject.Inject

class PlaceOrderUseCase @Inject constructor(
    private val ordersRepository: OrdersRepository,
) {
    suspend operator fun invoke(order: Order): Result<Unit> = ordersRepository.placeOrder(order)
}
