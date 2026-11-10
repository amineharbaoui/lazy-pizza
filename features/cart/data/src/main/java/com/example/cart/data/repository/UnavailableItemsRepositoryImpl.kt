package com.example.cart.data.repository

import com.example.cart.domain.model.UnavailableItem
import com.example.cart.domain.repository.UnavailableItemsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

// In memory: after a process death the dialog is lost, but the reordered items stay in the cart.
internal class UnavailableItemsRepositoryImpl @Inject constructor() : UnavailableItemsRepository {

    private val items = MutableStateFlow<List<UnavailableItem>>(emptyList())

    override fun observeUnavailableItems(): Flow<List<UnavailableItem>> = items

    override suspend fun saveUnavailableItems(items: List<UnavailableItem>) {
        this.items.value = items
    }

    override suspend fun clearUnavailableItems() {
        items.value = emptyList()
    }
}
