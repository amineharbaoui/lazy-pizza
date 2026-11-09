package com.example.cart.data.datasource

import androidx.room.withTransaction
import com.example.cart.data.datasource.db.CartDatabase
import com.example.cart.data.datasource.db.dao.CartDao
import com.example.cart.data.datasource.db.dao.CartMetadataDao
import com.example.cart.data.datasource.db.entity.CartMetadataEntity
import com.example.cart.data.mapper.CartDomainToEntityMapper
import com.example.cart.data.mapper.CartEntityToDomainMapper
import com.example.cart.domain.model.Cart
import com.example.cart.domain.model.CartItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

class CartLocalDataSourceImpl @Inject constructor(
    private val db: CartDatabase,
    private val cartDao: CartDao,
    private val cartMetadataDao: CartMetadataDao,
    private val entityToDomainMapper: CartEntityToDomainMapper,
    private val domainToEntityMapper: CartDomainToEntityMapper,
) : CartLocalDataSource {
    override fun observeCart(ownerKey: String): Flow<Cart> = cartDao.observeCartLines(ownerKey = ownerKey)
        .map { lines -> Cart(entityToDomainMapper.mapList(lines)) }

    override suspend fun getLastUpdatedAt(ownerKey: String): Instant? = cartMetadataDao.getMetadata(ownerKey)
        ?.let { Instant.ofEpochMilli(it.lastUpdatedAtMillis) }

    override suspend fun setLastUpdatedAt(
        ownerKey: String,
        at: Instant,
    ) {
        cartMetadataDao.upsert(
            CartMetadataEntity(
                ownerKey = ownerKey,
                lastUpdatedAtMillis = at.toEpochMilli(),
            ),
        )
    }

    override suspend fun addItem(
        ownerKey: String,
        item: CartItem,
    ) {
        when (item) {
            is CartItem.Other -> insertSimple(ownerKey = ownerKey, item = item)
            is CartItem.Pizza -> insertPizza(ownerKey = ownerKey, item = item)
        }
    }

    override suspend fun updateItemQuantity(
        ownerKey: String,
        lineId: String,
        quantity: Int,
    ) {
        val currentLine = cartDao.getCartLinesOnce(ownerKey)
            .firstOrNull { it.item.lineId == lineId }
            ?: return

        if (quantity <= 0) {
            cartDao.deleteToppingsForLine(lineId)
            cartDao.deleteItem(lineId)
        } else {
            cartDao.updateItem(currentLine.item.copy(quantity = quantity))
        }
    }

    override suspend fun removeItem(
        ownerKey: String,
        lineId: String,
    ) {
        cartDao.deleteToppingsForLine(lineId)
        cartDao.deleteItem(lineId)
    }

    override suspend fun clear(ownerKey: String) {
        cartDao.clearToppings(ownerKey)
        cartDao.clearItems(ownerKey)
        cartMetadataDao.clear(ownerKey)
    }

    override suspend fun transferCart(
        fromOwnerKey: String,
        toOwnerKey: String,
        at: Instant,
    ) = db.withTransaction {
        val sourceLines = cartDao.getCartLinesOnce(fromOwnerKey)
        if (sourceLines.isEmpty()) return@withTransaction

        sourceLines.forEach { line ->
            cartDao.insertItem(line.item.copy(ownerKey = toOwnerKey))
            cartDao.insertToppings(line.toppings.map { it.copy(ownerKey = toOwnerKey) })
        }

        cartDao.clearToppings(ownerKey = fromOwnerKey)
        cartDao.clearItems(ownerKey = fromOwnerKey)
        cartMetadataDao.clear(ownerKey = fromOwnerKey)

        setLastUpdatedAt(ownerKey = toOwnerKey, at = at)
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
}
