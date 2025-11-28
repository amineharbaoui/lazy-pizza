package com.example.cart.domain.repository

import com.example.domain.model.MenuItem
import kotlinx.coroutines.flow.Flow

interface RecommendedItemsRepository {
    suspend fun getRecommendedItems(): Flow<List<MenuItem>>
}