package com.example.cart.data.datasource

class FakeCartTransferLocalDataSource : CartTransferLocalDataSource {

    val transfers = mutableListOf<Pair<String, String>>()

    override suspend fun transferCart(
        fromOwnerKey: String,
        toOwnerKey: String,
    ) {
        transfers += fromOwnerKey to toOwnerKey
    }
}
