package com.example.data.datasource.local

import com.example.domain.model.Cart
import com.example.domain.model.CartItem
import com.example.domain.model.OtherCartItem
import com.example.domain.model.PizzaCartItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class CartLocalDataSource @Inject constructor(
    private val cartDao: CartDao,
    private val cartMetadataDao: CartMetadataDao,
    @param:CartTtl private val cartTtlSeconds: Long,
    @param:CartTouchThrottle private val cartTouchThrottleSeconds: Long,
) {
    private val touchThrottleMillis: Long get() = cartTouchThrottleSeconds.seconds.inWholeMilliseconds
    private var lastTouchWriteAt: Long = 0L

    fun observeCart(): Flow<Cart> = flow {
        expireIfNeeded()

        emitAll(
            cartDao.observeCartLines()
                .onEach { lines ->
                    if (lines.isNotEmpty()) touchCartIfStale()
                }
                .map { lines ->
                    val items: List<CartItem> = lines.map { it.toDomain() }
                    Cart(items)
                },
        )
    }

    suspend fun addItem(item: CartItem) {
        expireIfNeeded()
        when (item) {
            is OtherCartItem -> insertSimple(item)
            is PizzaCartItem -> insertPizza(item)
        }
        touchCart()
    }

    suspend fun updateItemQuantity(
        lineId: String,
        quantity: Int,
    ) {
        expireIfNeeded()
        val currentLine = cartDao.observeCartLines()
            .map { lines -> lines.firstOrNull { it.item.lineId == lineId } }
            .firstOrNull()
            ?: return

        if (quantity <= 0) {
            cartDao.deleteToppingsForLine(lineId)
            cartDao.deleteItem(lineId)
        } else {
            cartDao.updateItem(currentLine.item.copy(quantity = quantity))
        }
        touchCart()
    }

    suspend fun removeItem(lineId: String) {
        expireIfNeeded()
        cartDao.deleteToppingsForLine(lineId)
        cartDao.deleteItem(lineId)
        touchCart()
    }

    suspend fun clear() {
        cartDao.clearToppings()
        cartDao.clearItems()
        cartMetadataDao.clear()
    }

    private suspend fun insertSimple(item: OtherCartItem) {
        val entity = item.toEntity()
        cartDao.insertItem(entity)
        cartDao.deleteToppingsForLine(item.lineId)
    }

    private suspend fun insertPizza(item: PizzaCartItem) {
        val entity = item.toEntity()
        val toppingEntities = item.toppings.map { it.toEntity(item.lineId) }

        cartDao.insertItem(entity)
        cartDao.deleteToppingsForLine(item.lineId)
        cartDao.insertToppings(toppingEntities)
    }

    private suspend fun expireIfNeeded() {
        val metadata = cartMetadataDao.getMetadata() ?: return

        val now = System.currentTimeMillis()
        val age = now - metadata.lastUpdatedAtMillis

        if (age > cartTtlSeconds.minutes.inWholeMilliseconds) {
            cartDao.clearToppings()
            cartDao.clearItems()
            cartMetadataDao.clear()
        }
    }

    private suspend fun touchCartIfStale() {
        val now = System.currentTimeMillis()
        if (now - lastTouchWriteAt < touchThrottleMillis) return

        val metadata = cartMetadataDao.getMetadata()
        if (metadata == null || (now - metadata.lastUpdatedAtMillis) >= touchThrottleMillis) {
            cartMetadataDao.upsert(
                CartMetadataEntity(
                    id = 0,
                    lastUpdatedAtMillis = now,
                ),
            )
            lastTouchWriteAt = now
        }
    }

    private suspend fun touchCart() {
        val now = System.currentTimeMillis()
        cartMetadataDao.upsert(
            CartMetadataEntity(
                id = 0,
                lastUpdatedAtMillis = now,
            ),
        )
        lastTouchWriteAt = now
    }
}
