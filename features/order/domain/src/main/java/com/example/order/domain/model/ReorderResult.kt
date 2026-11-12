package com.example.order.domain.model

sealed interface ReorderResult {
    data object Added : ReorderResult

    data object NothingAvailable : ReorderResult

    data object MenuUnavailable : ReorderResult
}
