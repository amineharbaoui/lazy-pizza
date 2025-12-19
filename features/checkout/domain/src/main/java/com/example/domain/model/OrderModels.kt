package com.example.domain.model

import com.example.model.ProductCategory
import java.time.Instant

data class Order(
    val id: String = "",
    val userId: String,
    val createdAt: Instant,
    val pickup: PickupSelection,
    val comment: String,
    val items: List<OrderItem>,
    val total: Double,
)

sealed interface OrderItem {
    val productId: String
    val name: String
    val unitPrice: Double
    val quantity: Int
    val category: ProductCategory

    data class Pizza(
        override val productId: String,
        override val name: String,
        override val unitPrice: Double,
        override val quantity: Int,
        override val category: ProductCategory,
        val toppings: List<OrderTopping>,
    ) : OrderItem

    data class Other(
        override val productId: String,
        override val name: String,
        override val unitPrice: Double,
        override val quantity: Int,
        override val category: ProductCategory,
    ) : OrderItem
}

data class OrderTopping(
    val productId: String,
    val name: String,
    val unitPrice: Double,
    val quantity: Int,
)

sealed interface PickupSelection {
    data class Asap(val estimatedMinutes: Int = 15) : PickupSelection
    data class Scheduled(
        val dayId: String,
        val timeSlotId: String,
        val timeSlotLabel: String,
    ) : PickupSelection
}
