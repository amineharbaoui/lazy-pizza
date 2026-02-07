package com.example.order.data.mapper

import com.example.core.model.OrderStatus
import com.example.order.data.model.OrderDto
import com.example.order.data.model.OrderItemDto
import com.example.order.data.model.OrderToppingDto
import com.example.order.domain.model.Order
import com.example.order.domain.model.OrderItem
import javax.inject.Inject

class OrderDomainToDtoMapper @Inject constructor() {

    fun map(order: Order): OrderDto = OrderDto(
        orderNumber = order.orderNumber,
        userId = order.userId,
        createdAtEpochMs = order.createdAt.toEpochMilli(),
        pickupType = order.pickupType.name,
        pickupAtEpochMs = order.pickupAt.toEpochMilli(),
        status = OrderStatus.IN_PROGRESS.name,
        comment = order.comment,
        total = order.total,
        items = order.items.map { mapOrderItem(it) },
    )

    private fun mapOrderItem(item: OrderItem): OrderItemDto = OrderItemDto(
        productId = item.productId,
        name = item.name,
        unitPrice = item.unitPrice,
        quantity = item.quantity,
        toppings = item.toppings.map {
            OrderToppingDto(
                id = it.id,
                name = it.name,
                unitPrice = it.unitPrice,
                quantity = it.quantity,
            )
        },
        category = item.category.name,
    )
}
