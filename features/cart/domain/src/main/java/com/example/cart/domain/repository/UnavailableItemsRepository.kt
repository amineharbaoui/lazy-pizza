package com.example.cart.domain.repository

import com.example.cart.domain.model.UnavailableItem
import kotlinx.coroutines.flow.Flow

interface UnavailableItemsRepository {
    fun observeUnavailableItems(): Flow<List<UnavailableItem>>
    suspend fun saveUnavailableItems(items: List<UnavailableItem>)
    suspend fun clearUnavailableItems()
}
