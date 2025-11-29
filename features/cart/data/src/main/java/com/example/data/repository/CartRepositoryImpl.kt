package com.example.data.repository

import com.example.data.datasource.local.CartLocalDataSource
import com.example.domain.model.Cart
import com.example.domain.model.CartItem
import com.example.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val localDataSource: CartLocalDataSource,
) : CartRepository {

    override fun observeCart(): Flow<Cart> = localDataSource.observeCart()

    override suspend fun addItem(item: CartItem) {
        localDataSource.addItem(item)
    }

    override suspend fun updateItemQuantity(
        lineId: String,
        quantity: Int,
    ) {
        localDataSource.updateItemQuantity(
            lineId = lineId,
            quantity = quantity,
        )
    }

    override suspend fun removeItem(lineId: String) {
        localDataSource.removeItem(lineId)
    }

    override suspend fun clear() {
        localDataSource.clear()
    }
}
