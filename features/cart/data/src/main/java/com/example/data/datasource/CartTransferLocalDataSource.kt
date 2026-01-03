package com.example.data.datasource

import androidx.room.withTransaction
import com.example.data.datasource.db.CartDatabase
import com.example.data.datasource.db.dao.CartDao
import com.example.data.datasource.db.dao.CartMetadataDao
import com.example.data.datasource.db.entity.CartMetadataEntity
import javax.inject.Inject

class CartTransferLocalDataSource @Inject constructor(
    private val db: CartDatabase,
    private val cartDao: CartDao,
    private val metadataDao: CartMetadataDao,
) {
    suspend fun transferGuestToUser(userKey: String) = db.withTransaction {
        val guestKey = "guest"

        val guestLines = cartDao.getCartLinesOnce(guestKey)
        if (guestLines.isEmpty()) return@withTransaction

        guestLines.forEach { line ->
            cartDao.insertItem(line.item.copy(ownerKey = userKey))
            cartDao.insertToppings(line.toppings.map { it.copy(ownerKey = userKey) })
        }

        cartDao.clearToppings(ownerKey = guestKey)
        cartDao.clearItems(ownerKey = guestKey)
        metadataDao.clear(ownerKey = guestKey)

        metadataDao.upsert(
            CartMetadataEntity(
                ownerKey = userKey,
                lastUpdatedAtMillis = System.currentTimeMillis(),
            ),
        )
    }
}
