package com.example.domain.usecase

import com.example.domain.repository.RecommendedItemsRepository
import javax.inject.Inject

class ObserveRecommendedItemsUseCase @Inject constructor(private val recommendedItemsRepository: RecommendedItemsRepository) {
    operator fun invoke() = recommendedItemsRepository.getRecommendedItems()
}
