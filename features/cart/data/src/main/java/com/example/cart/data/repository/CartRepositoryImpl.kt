package com.example.cart.data.repository

import com.example.cart.data.datasource.CartLocalDataSource
import com.example.cart.data.di.CartTouchThrottle
import com.example.cart.data.di.CartTtl
import com.example.cart.data.mapper.CartOwnerToKeyMapper
import com.example.cart.domain.model.Cart
import com.example.cart.domain.model.CartItem
import com.example.cart.domain.model.CartOwner
import com.example.cart.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import java.time.Clock
import java.time.Duration
import java.time.Instant
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val localDataSource: CartLocalDataSource,
    private val ownerToKeyMapper: CartOwnerToKeyMapper,
    private val clock: Clock,
    @param:CartTtl private val cartTtlSeconds: Long,
    @param:CartTouchThrottle private val cartTouchThrottleSeconds: Long,
) : CartRepository {

    private val cartTtl: Duration get() = Duration.ofSeconds(cartTtlSeconds)
    private val touchThrottle: Duration get() = Duration.ofSeconds(cartTouchThrottleSeconds)
    private var lastTouchWriteAt: Instant = Instant.EPOCH

    override fun observeCart(owner: CartOwner): Flow<Cart> = flow {
        val ownerKey = ownerToKeyMapper.map(owner)
        expireIfNeeded(ownerKey)
        emitAll(
            localDataSource.observeCart(ownerKey = ownerKey)
                .onEach { cart -> if (cart.items.isNotEmpty()) touchCartIfStale(ownerKey) },
        )
    }

    override suspend fun addItem(
        owner: CartOwner,
        item: CartItem,
    ) {
        val ownerKey = ownerToKeyMapper.map(owner)
        expireIfNeeded(ownerKey)
        localDataSource.addItem(ownerKey = ownerKey, item = item)
        touchCart(ownerKey)
    }

    override suspend fun updateItemQuantity(
        owner: CartOwner,
        lineId: String,
        quantity: Int,
    ) {
        val ownerKey = ownerToKeyMapper.map(owner)
        expireIfNeeded(ownerKey)
        localDataSource.updateItemQuantity(ownerKey = ownerKey, lineId = lineId, quantity = quantity)
        touchCart(ownerKey)
    }

    override suspend fun removeItem(
        owner: CartOwner,
        lineId: String,
    ) {
        val ownerKey = ownerToKeyMapper.map(owner)
        expireIfNeeded(ownerKey)
        localDataSource.removeItem(ownerKey = ownerKey, lineId = lineId)
        touchCart(ownerKey)
    }

    override suspend fun clear(owner: CartOwner) {
        localDataSource.clear(ownerKey = ownerToKeyMapper.map(owner))
    }

    override suspend fun transferGuestCart(to: CartOwner.User) {
        localDataSource.transferCart(
            fromOwnerKey = ownerToKeyMapper.map(CartOwner.Guest),
            toOwnerKey = ownerToKeyMapper.map(to),
            at = clock.instant(),
        )
    }

    private suspend fun expireIfNeeded(ownerKey: String) {
        val lastUpdatedAt = localDataSource.getLastUpdatedAt(ownerKey) ?: return
        if (Duration.between(lastUpdatedAt, clock.instant()) > cartTtl) {
            localDataSource.clear(ownerKey)
        }
    }

    private suspend fun touchCartIfStale(ownerKey: String) {
        val now = clock.instant()
        if (Duration.between(lastTouchWriteAt, now) < touchThrottle) return

        val lastUpdatedAt = localDataSource.getLastUpdatedAt(ownerKey)
        if (lastUpdatedAt == null || Duration.between(lastUpdatedAt, now) >= touchThrottle) {
            touchCart(ownerKey)
        }
    }

    private suspend fun touchCart(ownerKey: String) {
        val now = clock.instant()
        localDataSource.setLastUpdatedAt(ownerKey = ownerKey, at = now)
        lastTouchWriteAt = now
    }
}
