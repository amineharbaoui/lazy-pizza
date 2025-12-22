package com.example.data.mapper

import com.example.data.model.OrderDto
import com.example.domain.model.Order
import com.example.domain.model.OrderItem
import com.example.domain.model.OrderTopping
import com.example.domain.model.PickupType
import com.example.model.OrderStatus
import com.example.model.ProductCategory
import java.time.Instant
import javax.inject.Inject

class OrderDtoToDomainMapper @Inject constructor() {
    fun map(dto: OrderDto) = Order(
        orderNumber = dto.orderNumber,
        userId = dto.userId,
        createdAt = Instant.ofEpochMilli(dto.createdAtEpochMs),
        pickupType = dto.pickupType.toPickupType(),
        pickupAt = Instant.ofEpochMilli(dto.pickupAtEpochMs),
        status = dto.status.toOrderStatus(),
        comment = dto.comment,
        total = dto.total,
        items = dto.items.map { item ->
            OrderItem(
                productId = item.productId,
                name = item.name,
                unitPrice = item.unitPrice,
                quantity = item.quantity,
                category = item.category.toProductCategory(),
                toppings = item.toppings.map { t ->
                    OrderTopping(
                        id = t.id,
                        name = t.name,
                        unitPrice = t.unitPrice,
                        quantity = t.quantity,
                    )
                },
            )
        },
    )

    private fun String.toPickupType() = PickupType.entries.firstOrNull { it.name == this } ?: PickupType.ASAP

    private fun String.toOrderStatus() = OrderStatus.entries.firstOrNull { it.name == this } ?: OrderStatus.IN_PROGRESS

    private fun String.toProductCategory() = ProductCategory.entries.firstOrNull { it.name == this } ?: ProductCategory.PIZZA
}
