package com.example.lazypizza.features.cart.domain.usecase

import com.example.lazypizza.features.cart.domain.repository.CartRepository
import javax.inject.Inject

class ClearCart @Inject constructor(
    private val repository: CartRepository,
) {
    suspend operator fun invoke() {
        repository.clear()
    }
}