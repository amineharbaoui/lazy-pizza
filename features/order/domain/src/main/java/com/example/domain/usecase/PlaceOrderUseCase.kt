package com.example.domain.usecase

import com.example.domain.model.Order
import com.example.domain.repository.OrdersRepository
import javax.inject.Inject

class PlaceOrderUseCase @Inject constructor(
    private val ordersRepository: OrdersRepository,
) {
    suspend operator fun invoke(order: Order): Result<Unit> = ordersRepository.placeOrder(order)
}
