package com.example.cart.domain.usecase

import com.example.cart.domain.repository.CartRepository
import javax.inject.Inject

class ClearCartUseCase @Inject constructor(
    private val repository: CartRepository,
) {
    suspend operator fun invoke() {
        repository.clear()
    }
}