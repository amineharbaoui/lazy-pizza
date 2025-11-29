package com.example.domain.usecase

import com.example.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveCartItemCountUseCase(private val cartRepository: CartRepository) {
    operator fun invoke(): Flow<Int> = cartRepository.observeCart()
        .map { cart -> cart.items.sumOf { it.quantity } }
}
