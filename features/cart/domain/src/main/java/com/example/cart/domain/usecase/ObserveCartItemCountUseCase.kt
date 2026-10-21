package com.example.cart.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveCartItemCountUseCase @Inject constructor(private val observeCart: ObserveCartUseCase) {
    operator fun invoke(): Flow<Int> = observeCart()
        .map { cart ->
            cart.items.sumOf { it.quantity }
        }
}
