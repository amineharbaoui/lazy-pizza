package com.example.domain.usecase

import com.example.domain.model.MenuItem
import com.example.domain.repository.RecommendedItemsRepository
import kotlinx.coroutines.flow.Flow

class ObserveRecommendedItemsUseCase(private val repository: RecommendedItemsRepository) {
    suspend operator fun invoke(): Flow<List<MenuItem>> = repository.getRecommendedItems()
}
