package com.example.cart.data.datasource

import com.example.cart.domain.model.Cart
import com.example.cart.domain.model.CartItem
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * In-memory cart with the Room DAO's REPLACE semantics: writing a line whose `lineId` already
 * exists overwrites it, it never sums quantities. `addItems` is all-or-nothing.
 */
class FakeCartLocalDataSource : CartLocalDataSource {

    private val linesByOwner = MutableStateFlow<Map<String, Map<String, CartItem>>>(emptyMap())

    private var writeLimit: Int? = null
    private var linesWritten = 0
    private var addItemsGate: CompletableDeferred<Unit>? = null

    /** Puts lines in the cart directly, bypassing the failure switch. */
    fun seed(
        ownerKey: String,
        items: List<CartItem>,
    ) {
        items.forEach { put(ownerKey, it) }
    }

    /** Single-line writes succeed until [lines] lines are written, then throw. A batch that would go past it throws and writes nothing. */
    fun failWritesAfterLines(lines: Int) {
        writeLimit = lines
        linesWritten = 0
    }

    fun stopFailingWrites() {
        writeLimit = null
    }

    /** Every `addItems` call suspends until [releaseAddItems]. */
    fun holdAddItems() {
        addItemsGate = CompletableDeferred()
    }

    fun releaseAddItems() {
        addItemsGate?.complete(Unit)
        addItemsGate = null
    }

    override fun observeCart(ownerKey: String): Flow<Cart> = linesByOwner.map { Cart(it[ownerKey].orEmpty().values.toList()) }

    override suspend fun addItem(
        ownerKey: String,
        item: CartItem,
    ) {
        ensureCanWrite(lineCount = 1)
        put(ownerKey, item)
        linesWritten++
    }

    override suspend fun addItems(
        ownerKey: String,
        items: List<CartItem>,
    ) {
        addItemsGate?.await()
        ensureCanWrite(lineCount = items.size)
        items.forEach { put(ownerKey, it) }
        linesWritten += items.size
    }

    override suspend fun updateItemQuantity(
        ownerKey: String,
        lineId: String,
        quantity: Int,
    ) {
        ensureCanWrite(lineCount = 1)
        linesByOwner.update { all ->
            val lines = all[ownerKey].orEmpty()
            val line = lines[lineId] ?: return@update all
            if (quantity <= 0) return@update all + (ownerKey to lines - lineId)
            val updated = when (line) {
                is CartItem.Other -> line.copy(quantity = quantity)
                is CartItem.Pizza -> line.copy(quantity = quantity)
            }
            all + (ownerKey to lines + (lineId to updated))
        }
        linesWritten++
    }

    override suspend fun removeItem(
        ownerKey: String,
        lineId: String,
    ) {
        linesByOwner.update { all -> all + (ownerKey to all[ownerKey].orEmpty() - lineId) }
    }

    override suspend fun clear(ownerKey: String) {
        linesByOwner.update { it - ownerKey }
    }

    private fun ensureCanWrite(lineCount: Int) {
        val limit = writeLimit ?: return
        if (linesWritten + lineCount > limit) throw FakeCartWriteException()
    }

    private fun put(
        ownerKey: String,
        item: CartItem,
    ) {
        linesByOwner.update { all -> all + (ownerKey to all[ownerKey].orEmpty() + (item.lineId to item)) }
    }
}

class FakeCartWriteException : Exception("Fake cart write failure")
