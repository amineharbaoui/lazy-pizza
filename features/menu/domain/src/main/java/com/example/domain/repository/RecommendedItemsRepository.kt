package com.example.domain.repository

import com.example.domain.model.MenuItem
import kotlinx.coroutines.flow.Flow

interface RecommendedItemsRepository {
    fun getRecommendedItems(): Flow<List<MenuItem>>
}
