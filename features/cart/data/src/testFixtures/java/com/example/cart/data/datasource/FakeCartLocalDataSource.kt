package com.example.cart.data.datasource

import com.example.cart.domain.model.Cart
import com.example.cart.domain.model.CartItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import java.time.Instant

internal class FakeCartLocalDataSource : CartLocalDataSource {

    /** Each owner's lines, in insertion order, like the Room table. */
    val lines = MutableStateFlow<Map<String, List<CartItem>>>(emptyMap())

    val lastUpdatedAt = mutableMapOf<String, Instant>()

    var failure: Exception? = null

    override fun observeCart(ownerKey: String): Flow<Cart> = lines
        .onStart { failure?.let { throw it } }
        .map { it[ownerKey].orEmpty() }
        .distinctUntilChanged()
        .map(::Cart)

    override suspend fun getLastUpdatedAt(ownerKey: String): Instant? {
        failIfAsked()
        return lastUpdatedAt[ownerKey]
    }

    override suspend fun setLastUpdatedAt(
        ownerKey: String,
        at: Instant,
    ) {
        failIfAsked()
        lastUpdatedAt[ownerKey] = at
    }

    override suspend fun addItem(
        ownerKey: String,
        item: CartItem,
    ) {
        failIfAsked()
        // Room's REPLACE deletes the old row and inserts a new one, at the end.
        updateLines(ownerKey) { current -> current.filterNot { it.lineId == item.lineId } + item }
    }

    override suspend fun replaceItem(
        ownerKey: String,
        item: CartItem,
    ) {
        failIfAsked()
        updateLines(ownerKey) { current -> current.map { if (it.lineId == item.lineId) item else it } }
    }

    override suspend fun updateItemQuantity(
        ownerKey: String,
        lineId: String,
        quantity: Int,
    ) {
        failIfAsked()
        updateLines(ownerKey) { current ->
            if (quantity <= 0) {
                current.filterNot { it.lineId == lineId }
            } else {
                current.map { if (it.lineId == lineId) it.withQuantity(quantity) else it }
            }
        }
    }

    override suspend fun removeItem(
        ownerKey: String,
        lineId: String,
    ) {
        failIfAsked()
        updateLines(ownerKey) { current -> current.filterNot { it.lineId == lineId } }
    }

    override suspend fun clear(ownerKey: String) {
        failIfAsked()
        lines.update { it - ownerKey }
        lastUpdatedAt.remove(ownerKey)
    }

    override suspend fun transferCart(
        fromOwnerKey: String,
        toOwnerKey: String,
        at: Instant,
    ) {
        failIfAsked()
        val moved = lines.value[fromOwnerKey].orEmpty()
        if (moved.isEmpty()) return
        lines.update { it - fromOwnerKey + (toOwnerKey to it[toOwnerKey].orEmpty() + moved) }
        lastUpdatedAt.remove(fromOwnerKey)
        lastUpdatedAt[toOwnerKey] = at
    }

    private fun updateLines(
        ownerKey: String,
        transform: (List<CartItem>) -> List<CartItem>,
    ) {
        lines.update { it + (ownerKey to transform(it[ownerKey].orEmpty())) }
    }

    private fun failIfAsked() {
        failure?.let { throw it }
    }

    private fun CartItem.withQuantity(quantity: Int): CartItem = when (this) {
        is CartItem.Other -> copy(quantity = quantity)
        is CartItem.Pizza -> copy(quantity = quantity)
    }
}
