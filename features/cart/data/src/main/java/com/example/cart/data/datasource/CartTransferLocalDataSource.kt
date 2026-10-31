package com.example.cart.data.datasource

interface CartTransferLocalDataSource {
    suspend fun transferCart(
        fromOwnerKey: String,
        toOwnerKey: String,
    )
}
