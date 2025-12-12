package com.example.domain.usecase

import com.example.domain.repository.RecommendedItemsRepository

class ObserveRecommendedItemsUseCase(private val recommendedItemsRepository: RecommendedItemsRepository) {
    suspend operator fun invoke() = recommendedItemsRepository.getRecommendedItems()
}
