package com.example.domain.usecase

import com.example.domain.repository.CartRepository

class ClearCartUseCase(private val repository: CartRepository) {
    suspend operator fun invoke() {
        repository.clear()
    }
}
