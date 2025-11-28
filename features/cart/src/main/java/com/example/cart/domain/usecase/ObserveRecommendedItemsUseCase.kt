package com.example.cart.domain.usecase

import com.example.cart.domain.repository.RecommendedItemsRepository
import com.example.domain.model.MenuItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveRecommendedItemsUseCase @Inject constructor(
    private val repository: RecommendedItemsRepository,
) {
    suspend operator fun invoke(): Flow<List<MenuItem>> = repository.getRecommendedItems()
}

