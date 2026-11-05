package com.example.order.domain.model

sealed interface ReorderResult {
    /** At least one item was added to the cart. [skipped] is empty when everything was added. */
    data class Added(val skipped: List<SkippedReorderItem>) : ReorderResult

    /** Nothing in the order is on the menu anymore. The cart is unchanged. */
    data class NothingAvailable(val skipped: List<SkippedReorderItem>) : ReorderResult

    data class Failed(val error: ReorderError) : ReorderResult
}

sealed interface SkippedReorderItem {
    data class Product(val name: String) : SkippedReorderItem
    data class Topping(
        val toppingName: String,
        val pizzaName: String,
    ) : SkippedReorderItem
}

sealed interface ReorderError {
    val isRetryable: Boolean

    data object MenuUnavailable : ReorderError {
        override val isRetryable = true
    }

    data object CartUpdateFailed : ReorderError {
        override val isRetryable = true
    }
}
