package com.example.data.model

data class OrderDto(
    val userId: String,
    val createdAtEpochMs: Long,
    val pickupType: String,
    val scheduledDayId: String?,
    val scheduledTimeSlotId: String?,
    val scheduledTimeSlotLabel: String?,
    val comment: String,
    val total: Double,
    val items: List<OrderItemDto>,
)

data class OrderItemDto(
    val productId: String,
    val name: String,
    val unitPrice: Double,
    val quantity: Int,
    val category: String,
    val toppings: List<OrderToppingDto> = emptyList(),
)

data class OrderToppingDto(
    val productId: String,
    val name: String,
    val unitPrice: Double,
    val quantity: Int,
)
