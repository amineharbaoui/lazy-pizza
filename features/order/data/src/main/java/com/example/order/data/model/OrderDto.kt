package com.example.order.data.model

import androidx.annotation.Keep

@Keep
data class OrderDto(
    val orderNumber: String = "",
    val userId: String = "",
    val createdAtEpochMs: Long = 0L,
    val pickupType: String = "", // "ASAP" / "SCHEDULED"
    val pickupAtEpochMs: Long = 0L,
    val status: String = "", // "COMPLETED" / "CANCELED" / "IN_PROGRESS"
    val comment: String = "",
    val total: Double = 0.0,
    val items: List<OrderItemDto> = emptyList(),
)

@Keep
data class OrderItemDto(
    val productId: String = "",
    val name: String = "",
    val unitPrice: Double = 0.0,
    val quantity: Int = 0,
    val category: String = "", // "PIZZA" / ...
    val toppings: List<OrderToppingDto> = emptyList(),
)

@Keep
data class OrderToppingDto(
    val id: String = "",
    val name: String = "",
    val unitPrice: Double = 0.0,
    val quantity: Int = 0,
)
