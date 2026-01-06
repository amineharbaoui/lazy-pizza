package com.example.domain.usecase

import com.example.domain.model.Order
import com.example.domain.repository.OrdersRepository
import com.example.model.OrderStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

class ObserveOrdersUseCase @Inject constructor(
    private val repository: OrdersRepository,
) {
    operator fun invoke(userId: String): Flow<List<Order>> = repository.observeMyOrders(userId)
        .map { orders ->
            orders.map { order ->
                if (order.status == OrderStatus.IN_PROGRESS && order.pickupAt.isBefore(Instant.now())) {
                    order.copy(status = OrderStatus.COMPLETED)
                } else {
                    order
                }
            }
        }
}
