package com.example.order.domain.model

sealed interface ReorderResult {
    /** At least one item of the order was added to the cart. */
    data object Added : ReorderResult

    /** No item of the order is still on the menu: nothing was added. */
    data object NothingAvailable : ReorderResult

    /** The menu can't be loaded: nothing was added. */
    data object MenuUnavailable : ReorderResult
}
