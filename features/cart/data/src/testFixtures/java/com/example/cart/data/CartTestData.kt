package com.example.cart.data

import com.example.cart.data.datasource.FakeCartLocalDataSource
import com.example.cart.data.mapper.CartOwnerToKeyMapper
import com.example.cart.data.repository.CartRepositoryImpl
import com.example.cart.data.repository.UnavailableItemsRepositoryImpl
import com.example.cart.domain.model.CartItem
import com.example.cart.domain.model.CartOwner
import com.example.cart.domain.model.UnavailableItem
import com.example.cart.domain.repository.CartRepository
import com.example.cart.domain.repository.UnavailableItemsRepository
import java.time.Clock
import java.time.Duration
import java.time.Instant

class CartTestData(private val clock: Clock) {

    private val localDataSource = FakeCartLocalDataSource()
    private val ownerToKeyMapper = CartOwnerToKeyMapper()

    val cartRepository: CartRepository = CartRepositoryImpl(
        localDataSource = localDataSource,
        ownerToKeyMapper = ownerToKeyMapper,
        clock = clock,
        cartTtlSeconds = CART_TTL.seconds,
        cartTouchThrottleSeconds = CART_TOUCH_THROTTLE.seconds,
    )

    val unavailableItemsRepository: UnavailableItemsRepository = UnavailableItemsRepositoryImpl()

    /** Replaces the owner's cart with these lines, in this order, last updated at [updatedAt]. */
    fun givenCart(
        owner: CartOwner,
        vararg items: CartItem,
        updatedAt: Instant = clock.instant(),
    ) {
        val ownerKey = ownerToKeyMapper.map(owner)
        localDataSource.lines.value += ownerKey to items.toList()
        localDataSource.lastUpdatedAt[ownerKey] = updatedAt
    }

    /** The owner's lines as stored, in cart order. */
    fun cartLines(owner: CartOwner): List<CartItem> = localDataSource.lines.value[ownerToKeyMapper.map(owner)].orEmpty()

    /** The items and toppings a reorder left out, waiting for the Cart screen. */
    suspend fun givenUnavailableItems(vararg items: UnavailableItem) {
        unavailableItemsRepository.saveUnavailableItems(items.toList())
    }

    fun givenFailure(error: Exception = IllegalStateException(FORCED_FAILURE)) {
        localDataSource.failure = error
    }

    companion object {
        /** How long a cart lives without a change. */
        val CART_TTL: Duration = Duration.ofMinutes(30)
        val CART_TOUCH_THROTTLE: Duration = Duration.ofMinutes(1)

        private const val FORCED_FAILURE = "Forced failure"
    }
}
