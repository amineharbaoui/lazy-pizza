package com.example.ui.checkout

sealed interface CheckoutEvent {
    data object NavigateToAuth : CheckoutEvent
    data object OrderPlaced : CheckoutEvent
    data object ShowScheduleBottomSheet : CheckoutEvent
}
