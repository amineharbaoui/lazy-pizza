package com.example.order.ui.pastorders

import com.example.core.model.OrderStatus

sealed interface OrderUiState {
    data object Loading : OrderUiState
    data object NotLoggedIn : OrderUiState
    data class Ready(
        val orders: List<OrderDisplayModel>,
        val isReordering: Boolean = false,
        val dialog: ReorderDialog? = null,
        val navigation: OrdersNavigation? = null,
    ) : OrderUiState

    data object Error : OrderUiState
}

enum class ReorderDialog {
    NOTHING_AVAILABLE,
    FAILED,
}

enum class OrdersNavigation {
    CART,
}

data class OrderDisplayModel(
    val orderNumber: String,
    val dateTimeLabel: String,
    val items: List<OrderItemDisplayModel>,
    val totalAmountLabel: String,
    val status: OrderStatus,
)

data class OrderItemDisplayModel(
    val quantity: Int,
    val name: String,
    val toppings: List<OrderToppingDisplayModel>,
)

data class OrderToppingDisplayModel(
    val quantity: Int,
    val name: String,
)
