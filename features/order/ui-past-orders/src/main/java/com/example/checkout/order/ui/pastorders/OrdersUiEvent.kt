package com.example.checkout.order.ui.pastorders

sealed interface OrdersUiEvent {
    data object NavigateToCart : OrdersUiEvent
    data class ShowOrderAgainError(val error: OrderAgainErrorUi) : OrdersUiEvent
}

enum class OrderAgainErrorUi {
    MENU_UNAVAILABLE,
    CART_UPDATE_FAILED,
}
