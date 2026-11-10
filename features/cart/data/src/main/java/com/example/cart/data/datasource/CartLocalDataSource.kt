package com.example.cart.data.datasource

import com.example.cart.domain.model.Cart
import com.example.cart.domain.model.CartItem
import kotlinx.coroutines.flow.Flow
import java.time.Instant

internal interface CartLocalDataSource {
    fun observeCart(ownerKey: String): Flow<Cart>
    suspend fun getLastUpdatedAt(ownerKey: String): Instant?
    suspend fun setLastUpdatedAt(
        ownerKey: String,
        at: Instant,
    )
    suspend fun addItem(
        ownerKey: String,
        item: CartItem,
    )

    /** Sets a line's quantity, or removes the line when [quantity] is 0 or less. Does nothing when the line isn't there. */
    suspend fun updateItemQuantity(
        ownerKey: String,
        lineId: String,
        quantity: Int,
    )
    suspend fun removeItem(
        ownerKey: String,
        lineId: String,
    )

    /** Removes the owner's lines and last update. */
    suspend fun clear(ownerKey: String)

    /** Moves every line of [fromOwnerKey] to [toOwnerKey], updated [at]. Does nothing when [fromOwnerKey] has none. */
    suspend fun transferCart(
        fromOwnerKey: String,
        toOwnerKey: String,
        at: Instant,
    )
}
