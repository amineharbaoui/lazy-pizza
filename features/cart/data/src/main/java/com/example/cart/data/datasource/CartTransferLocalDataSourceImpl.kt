package com.example.cart.data.datasource

import androidx.room.withTransaction
import com.example.cart.data.datasource.db.CartDatabase
import com.example.cart.data.datasource.db.dao.CartDao
import com.example.cart.data.datasource.db.dao.CartMetadataDao
import com.example.cart.data.datasource.db.entity.CartMetadataEntity
import javax.inject.Inject

class CartTransferLocalDataSourceImpl @Inject constructor(
    private val db: CartDatabase,
    private val cartDao: CartDao,
    private val metadataDao: CartMetadataDao,
) : CartTransferLocalDataSource {
    override suspend fun transferCart(
        fromOwnerKey: String,
        toOwnerKey: String,
    ): Unit = db.withTransaction {
        val sourceLines = cartDao.getCartLinesOnce(fromOwnerKey)
        if (sourceLines.isEmpty()) return@withTransaction

        sourceLines.forEach { line ->
            cartDao.insertItem(line.item.copy(ownerKey = toOwnerKey))
            cartDao.insertToppings(line.toppings.map { it.copy(ownerKey = toOwnerKey) })
        }

        cartDao.clearToppings(ownerKey = fromOwnerKey)
        cartDao.clearItems(ownerKey = fromOwnerKey)
        metadataDao.clear(ownerKey = fromOwnerKey)

        metadataDao.upsert(
            CartMetadataEntity(
                ownerKey = toOwnerKey,
                lastUpdatedAtMillis = System.currentTimeMillis(),
            ),
        )
    }
}
