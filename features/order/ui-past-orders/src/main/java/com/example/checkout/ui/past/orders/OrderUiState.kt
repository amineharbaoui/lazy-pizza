package com.example.checkout.ui.past.orders

import com.example.model.OrderStatus

sealed interface OrderUiState {
    object Loading : OrderUiState
    data class Ready(
        val orders: List<OrderUi>,
    ) : OrderUiState

    data class Error(
        val message: String,
    ) : OrderUiState
}

data class OrderUi(
    val orderNumberLabel: String, // "Order #12347"
    val dateTimeLabel: String, // "September 25, 12:15"
    val items: List<OrderItem>, // ["1 x Margherita", "2 x Pepsi", ...]
    val totalAmountLabel: String, // "$25.45"
    val status: OrderStatus,
)

data class OrderItem(
    val name: String,
    val toppings: List<String>,
)
