package com.example.cart.domain.usecase

import com.example.cart.domain.model.UnavailableItem
import com.example.cart.domain.repository.UnavailableItemsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUnavailableItemsUseCase @Inject constructor(
    private val repository: UnavailableItemsRepository,
) {
    operator fun invoke(): Flow<List<UnavailableItem>> = repository.observeUnavailableItems()
}
