package com.example.domain.model

import com.example.model.OrderStatus
import com.example.model.ProductCategory
import java.time.Instant

data class Order(
    val orderNumber: String,
    val userId: String,
    val createdAt: Instant,
    val pickupType: PickupType,
    val pickupAt: Instant,
    val status: OrderStatus,
    val comment: String,
    val items: List<OrderItem>,
    val total: Double,
)

data class OrderItem(
    val productId: String,
    val name: String,
    val unitPrice: Double,
    val quantity: Int,
    val category: ProductCategory,
    val toppings: List<OrderTopping> = emptyList(),
)

data class OrderTopping(
    val id: String,
    val name: String,
    val unitPrice: Double,
    val quantity: Int,
)

enum class PickupType {
    ASAP,
    SCHEDULED,
}
