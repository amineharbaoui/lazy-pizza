package com.example.cart.domain.repository

import com.example.cart.domain.model.UnavailableItem
import kotlinx.coroutines.flow.Flow

/** The items and toppings the last reorder left out, until the user dismisses them on the Cart screen. */
interface UnavailableItemsRepository {
    fun observeUnavailableItems(): Flow<List<UnavailableItem>>
    suspend fun saveUnavailableItems(items: List<UnavailableItem>)
    suspend fun clearUnavailableItems()
}
