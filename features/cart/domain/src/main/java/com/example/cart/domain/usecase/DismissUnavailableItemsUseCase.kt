package com.example.cart.domain.usecase

import com.example.cart.domain.repository.UnavailableItemsRepository
import javax.inject.Inject

class DismissUnavailableItemsUseCase @Inject constructor(
    private val repository: UnavailableItemsRepository,
) {
    suspend operator fun invoke() {
        repository.clearUnavailableItems()
    }
}
