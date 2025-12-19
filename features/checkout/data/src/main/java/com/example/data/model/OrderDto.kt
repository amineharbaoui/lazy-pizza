package com.example.data.model

data class OrderDto(
    val userId: String = "",
    val createdAtEpochMs: Long = 0L,
    val pickupType: String = "", // "ASAP" / "SCHEDULED"
    val scheduledDayId: String? = null,
    val scheduledTimeSlotId: String? = null,
    val scheduledTimeSlotLabel: String? = null,
    val comment: String = "",
    val total: Double = 0.0,
    val items: List<OrderItemDto> = emptyList(),
)

data class OrderItemDto(
    val productId: String = "",
    val name: String = "",
    val unitPrice: Double = 0.0,
    val quantity: Int = 0,
    val category: String? = null,
    val toppings: List<OrderToppingDto> = emptyList(),
)

data class OrderToppingDto(
    val productId: String = "",
    val name: String = "",
    val unitPrice: Double = 0.0,
    val quantity: Int = 0,
)
