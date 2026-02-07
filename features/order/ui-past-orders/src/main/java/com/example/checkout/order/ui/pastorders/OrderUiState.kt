package com.example.checkout.order.ui.pastorders

import com.example.model.OrderStatus

sealed interface OrderUiState {
    object Loading : OrderUiState
    object NotLoggedIn : OrderUiState
    data class Ready(
        val orders: List<OrderUi>,
    ) : OrderUiState

    data class Error(
        val message: String,
    ) : OrderUiState
}

data class OrderUi(
    val orderNumberLabel: String,
    val dateTimeLabel: String,
    val items: List<OrderItem>,
    val totalAmountLabel: String,
    val status: OrderStatus,
)

data class OrderItem(
    val name: String,
    val toppings: List<String>,
)
