package com.example.menu.domain.repository

import com.example.menu.domain.model.MenuItem
import kotlinx.coroutines.flow.Flow

interface RecommendedItemsRepository {
    fun getRecommendedItems(): Flow<List<MenuItem>>
}
