package com.example.cart.domain.usecase

import com.example.cart.domain.repository.CartRepository
import javax.inject.Inject

class RemoveCartItemUseCase @Inject constructor(
    private val repository: CartRepository,
) {
    suspend operator fun invoke(lineId: String) {
        repository.removeItem(lineId)
    }
}