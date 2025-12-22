package com.example.domain.usecase

import com.example.domain.repository.CartRepository
import javax.inject.Inject

class UpdateCartItemQuantityUseCase @Inject constructor(private val repository: CartRepository) {
    suspend operator fun invoke(
        lineId: String,
        quantity: Int,
    ) {
        repository.updateItemQuantity(lineId, quantity)
    }
}
