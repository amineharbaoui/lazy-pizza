package com.example.domain.usecase

import com.example.domain.model.Order
import com.example.domain.repository.OrdersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveOrdersUseCase @Inject constructor(
    private val repository: OrdersRepository,
) {
    operator fun invoke(userId: String): Flow<List<Order>> = repository.observeMyOrders(userId)
}
