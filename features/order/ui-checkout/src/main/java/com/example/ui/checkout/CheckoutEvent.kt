package com.example.ui.checkout

sealed interface CheckoutEvent {
    data object NavigateToAuth : CheckoutEvent
    data object ShowScheduleBottomSheet : CheckoutEvent
    data class OrderPlaced(
        val orderNumber: String,
        val pickupTime: String,
    ) : CheckoutEvent
}
