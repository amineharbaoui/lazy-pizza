package com.example.data.repository

import com.example.auth.domain.repository.SessionRepository
import com.example.data.datasource.CartLocalDataSource
import com.example.data.datasource.CartTransferLocalDataSource
import com.example.domain.model.Cart
import com.example.domain.model.CartItem
import com.example.domain.repository.CartRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class CartRepositoryImpl @Inject constructor(
    private val localDataSource: CartLocalDataSource,
    private val transferLocalDataSource: CartTransferLocalDataSource,
    private val sessionRepository: SessionRepository,
) : CartRepository {

    companion object {
        private const val PREFIX_USER = "user:"
        private const val KEY_GUEST = "guest"

        private fun getOwnerKey(userId: String?): String = if (userId == null) KEY_GUEST else "$PREFIX_USER$userId"
    }

    private suspend fun currentOwnerKey(): String = getOwnerKey(sessionRepository.currentUserUid())

    override fun observeCart(): Flow<Cart> = sessionRepository.userIdFlow
        .map { uid -> getOwnerKey(uid) }
        .flatMapLatest { ownerKey -> localDataSource.observeCart(ownerKey) }

    override suspend fun addItem(item: CartItem) {
        localDataSource.addItem(ownerKey = currentOwnerKey(), item = item)
    }

    override suspend fun updateItemQuantity(
        lineId: String,
        quantity: Int,
    ) {
        localDataSource.updateItemQuantity(ownerKey = currentOwnerKey(), lineId = lineId, quantity = quantity)
    }

    override suspend fun removeItem(lineId: String) {
        localDataSource.removeItem(ownerKey = currentOwnerKey(), lineId = lineId)
    }

    override suspend fun clear() {
        localDataSource.clear(ownerKey = currentOwnerKey())
    }

    override suspend fun transferGuestCartToUser(userId: String) {
        transferLocalDataSource.transferGuestToUser(userKey = "user:$userId")
    }

    override suspend fun clearUserCart(userId: String) {
        localDataSource.clear(ownerKey = "user:$userId")
    }
}
