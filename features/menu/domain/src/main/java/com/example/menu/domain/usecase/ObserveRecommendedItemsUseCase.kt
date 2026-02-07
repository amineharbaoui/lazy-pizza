package com.example.menu.domain.usecase

import com.example.menu.domain.repository.RecommendedItemsRepository
import javax.inject.Inject

class ObserveRecommendedItemsUseCase @Inject constructor(private val recommendedItemsRepository: RecommendedItemsRepository) {
    operator fun invoke() = recommendedItemsRepository.getRecommendedItems()
}
