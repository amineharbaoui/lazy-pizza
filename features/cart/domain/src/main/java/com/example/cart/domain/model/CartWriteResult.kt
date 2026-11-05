package com.example.cart.domain.model

sealed interface CartWriteResult {
    data object Success : CartWriteResult

    /** Retryable: storage error, nothing was written. */
    data object Failed : CartWriteResult
}
