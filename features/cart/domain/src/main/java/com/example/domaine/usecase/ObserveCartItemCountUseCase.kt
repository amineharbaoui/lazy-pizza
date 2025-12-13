package com.example.domaine.usecase

import com.example.domaine.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveCartItemCountUseCase @Inject constructor(private val cartRepository: CartRepository) {
    operator fun invoke(): Flow<Int> = cartRepository.observeCart()
        .map { cart ->
            cart.items.sumOf { it.quantity }
        }
}
