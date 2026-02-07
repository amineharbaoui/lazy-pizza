package com.example.cart.data.datasource

import androidx.room.withTransaction
import com.example.cart.data.datasource.db.CartDatabase
import com.example.cart.data.datasource.db.dao.CartDao
import com.example.cart.data.datasource.db.dao.CartMetadataDao
import com.example.cart.data.datasource.db.entity.CartMetadataEntity
import javax.inject.Inject

class CartTransferLocalDataSource @Inject constructor(
    private val db: CartDatabase,
    private val cartDao: CartDao,
    private val metadataDao: CartMetadataDao,
) {
    companion object {
        private const val GUEST_KEY = "guest"
    }

    suspend fun transferGuestToUser(userKey: String) = db.withTransaction {
        val guestLines = cartDao.getCartLinesOnce(GUEST_KEY)
        if (guestLines.isEmpty()) return@withTransaction

        guestLines.forEach { line ->
            cartDao.insertItem(line.item.copy(ownerKey = userKey))
            cartDao.insertToppings(line.toppings.map { it.copy(ownerKey = userKey) })
        }

        cartDao.clearToppings(ownerKey = GUEST_KEY)
        cartDao.clearItems(ownerKey = GUEST_KEY)
        metadataDao.clear(ownerKey = GUEST_KEY)

        metadataDao.upsert(
            CartMetadataEntity(
                ownerKey = userKey,
                lastUpdatedAtMillis = System.currentTimeMillis(),
            ),
        )
    }
}
