package com.example.domain.usecase

import com.example.domain.repository.CartRepository

class RemoveCartItemUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(lineId: String) {
        repository.removeItem(lineId)
    }
}
