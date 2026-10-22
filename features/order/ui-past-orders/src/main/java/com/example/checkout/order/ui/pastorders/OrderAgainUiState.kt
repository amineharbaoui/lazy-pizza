package com.example.checkout.order.ui.pastorders

sealed interface OrderAgainUiState {
    data object Idle : OrderAgainUiState

    /** Every "Order again" button is disabled. */
    data object InProgress : OrderAgainUiState

    /** Dialog with "Go to cart" and "Close". */
    data class SomeSkipped(val skippedItems: List<SkippedItemUi>) : OrderAgainUiState

    /** Dialog with "Close" only. */
    data class NothingAvailable(val skippedItems: List<SkippedItemUi>) : OrderAgainUiState
}

sealed interface SkippedItemUi {
    data class Product(val name: String) : SkippedItemUi
    data class Topping(
        val toppingName: String,
        val pizzaName: String,
    ) : SkippedItemUi
}
