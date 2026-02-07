package com.example.order.domain.usecase

import com.example.core.model.OrderStatus
import com.example.order.domain.model.Order
import com.example.order.domain.repository.OrdersRepository
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
