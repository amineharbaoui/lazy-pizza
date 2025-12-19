package com.example.data.mapper

import com.example.data.model.OrderDto
import com.example.data.model.OrderItemDto
import com.example.data.model.OrderToppingDto
import com.example.domain.model.Order
import com.example.domain.model.OrderItem
import com.example.domain.model.PickupSelection

fun Order.toDto(): OrderDto = OrderDto(
    userId = userId,
    createdAtEpochMs = createdAt.toEpochMilli(),
    pickupType = when (pickup) {
        is PickupSelection.Asap -> "ASAP"
        is PickupSelection.Scheduled -> "SCHEDULED"
    },
    scheduledDayId = (pickup as? PickupSelection.Scheduled)?.dayId,
    scheduledTimeSlotId = (pickup as? PickupSelection.Scheduled)?.timeSlotId,
    scheduledTimeSlotLabel = (pickup as? PickupSelection.Scheduled)?.timeSlotLabel,
    comment = comment,
    total = total,
    items = items.map { it.toDto() },
)

private fun OrderItem.toDto(): OrderItemDto = when (this) {
    is OrderItem.Pizza -> OrderItemDto(
        productId = productId,
        name = name,
        unitPrice = unitPrice,
        quantity = quantity,
        toppings = toppings.map {
            OrderToppingDto(
                productId = it.id,
                name = it.name,
                unitPrice = it.unitPrice,
                quantity = it.quantity,
            )
        },
        category = category.name,
    )

    is OrderItem.Other -> OrderItemDto(
        productId = productId,
        name = name,
        unitPrice = unitPrice,
        quantity = quantity,
        category = category.name,
    )
}
