package com.example.cart.data.datasource

import com.example.cart.data.datasource.db.dao.CartDao
import com.example.cart.data.datasource.db.dao.CartMetadataDao
import com.example.cart.data.datasource.db.entity.CartMetadataEntity
import com.example.cart.data.di.CartTouchThrottle
import com.example.cart.data.di.CartTtl
import com.example.cart.data.mapper.CartDomainToEntityMapper
import com.example.cart.data.mapper.CartEntityToDomainMapper
import com.example.cart.domain.model.Cart
import com.example.cart.domain.model.CartItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class CartLocalDataSource @Inject constructor(
    private val cartDao: CartDao,
    private val cartMetadataDao: CartMetadataDao,
    private val entityToDomainMapper: CartEntityToDomainMapper,
    private val domainToEntityMapper: CartDomainToEntityMapper,
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
                    val items: List<CartItem> = entityToDomainMapper.mapList(lines)
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
        val currentLine = cartDao.getCartLinesOnce(ownerKey)
            .firstOrNull { it.item.lineId == lineId }
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
        val entity = domainToEntityMapper.mapItem(item, ownerKey)
        cartDao.insertItem(entity)
        cartDao.deleteToppingsForLine(item.lineId)
    }

    private suspend fun insertPizza(
        ownerKey: String,
        item: CartItem.Pizza,
    ) {
        val entity = domainToEntityMapper.mapItem(item, ownerKey)
        val toppingEntities = domainToEntityMapper.mapToppings(
            input = item.toppings,
            ownerKey = ownerKey,
            lineId = item.lineId,
        )

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
