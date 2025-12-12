package com.example.data.datasource

import com.example.data.datasource.db.dao.CartDao
import com.example.data.datasource.db.dao.CartMetadataDao
import com.example.data.datasource.db.entity.CartMetadataEntity
import com.example.data.datasource.mapper.toDomain
import com.example.data.datasource.mapper.toEntity
import com.example.data.datasource.util.CartTouchThrottle
import com.example.data.datasource.util.CartTtl
import com.example.domain.model.Cart
import com.example.domain.model.CartItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class CartLocalDataSource @Inject constructor(
    private val cartDao: CartDao,
    private val cartMetadataDao: CartMetadataDao,
    @param:CartTtl private val cartTtlSeconds: Long,
    @param:CartTouchThrottle private val cartTouchThrottleSeconds: Long,
) {
    private val touchThrottleMillis: Long get() = cartTouchThrottleSeconds.seconds.inWholeMilliseconds
    private var lastTouchWriteAt: Long = 0L

    fun observeCart(ownerKey: String): Flow<Cart> = flow {
        expireIfNeeded(ownerKey)
        emitAll(
            cartDao.observeCartLines(ownerKey = ownerKey)
                .onEach { lines ->
                    if (lines.isNotEmpty()) touchCartIfStale(ownerKey)
                }
                .map { lines ->
                    val items: List<CartItem> = lines.map { it.toDomain() }
                    Cart(items)
                },
        )
    }

    suspend fun addItem(
        ownerKey: String,
        item: CartItem,
    ) {
        expireIfNeeded(ownerKey)
        when (item) {
            is CartItem.Other -> insertSimple(ownerKey = ownerKey, item = item)
            is CartItem.Pizza -> insertPizza(ownerKey = ownerKey, item = item)
        }
        touchCart(ownerKey)
    }

    suspend fun updateItemQuantity(
        ownerKey: String,
        lineId: String,
        quantity: Int,
    ) {
        expireIfNeeded(ownerKey)
        val currentLine = cartDao.observeCartLines(ownerKey)
            .map { lines -> lines.firstOrNull { it.item.lineId == lineId } }
            .firstOrNull()
            ?: return

        if (quantity <= 0) {
            cartDao.deleteToppingsForLine(lineId)
            cartDao.deleteItem(lineId)
        } else {
            cartDao.updateItem(currentLine.item.copy(quantity = quantity))
        }
        touchCart(ownerKey)
    }

    suspend fun removeItem(
        ownerKey: String,
        lineId: String,
    ) {
        expireIfNeeded(ownerKey)
        cartDao.deleteToppingsForLine(lineId)
        cartDao.deleteItem(lineId)
        touchCart(ownerKey)
    }

    suspend fun clear(ownerKey: String) {
        cartDao.clearToppings(ownerKey)
        cartDao.clearItems(ownerKey)
        cartMetadataDao.clear(ownerKey)
    }

    private suspend fun insertSimple(
        ownerKey: String,
        item: CartItem.Other,
    ) {
        val entity = item.toEntity(ownerKey)
        cartDao.insertItem(entity)
        cartDao.deleteToppingsForLine(item.lineId)
    }

    private suspend fun insertPizza(
        ownerKey: String,
        item: CartItem.Pizza,
    ) {
        val entity = item.toEntity(ownerKey)
        val toppingEntities = item.toppings.map { it.toEntity(ownerKey = ownerKey, lineId = item.lineId) }

        cartDao.insertItem(entity)
        cartDao.deleteToppingsForLine(item.lineId)
        cartDao.insertToppings(toppingEntities)
    }

    private suspend fun expireIfNeeded(ownerKey: String) {
        val metadata = cartMetadataDao.getMetadata(ownerKey) ?: return

        val now = System.currentTimeMillis()
        val age = now - metadata.lastUpdatedAtMillis

        if (age > cartTtlSeconds.seconds.inWholeMilliseconds) {
            cartDao.clearToppings(ownerKey)
            cartDao.clearItems(ownerKey)
            cartMetadataDao.clear(ownerKey)
        }
    }

    private suspend fun touchCartIfStale(ownerKey: String) {
        val now = System.currentTimeMillis()
        if (now - lastTouchWriteAt < touchThrottleMillis) return

        val metadata = cartMetadataDao.getMetadata(ownerKey)
        if (metadata == null || (now - metadata.lastUpdatedAtMillis) >= touchThrottleMillis) {
            cartMetadataDao.upsert(
                CartMetadataEntity(
                    ownerKey = ownerKey,
                    lastUpdatedAtMillis = now,
                ),
            )
            lastTouchWriteAt = now
        }
    }

    private suspend fun touchCart(ownerKey: String) {
        val now = System.currentTimeMillis()
        cartMetadataDao.upsert(
            CartMetadataEntity(
                ownerKey = ownerKey,
                lastUpdatedAtMillis = now,
            ),
        )
        lastTouchWriteAt = now
    }
}
