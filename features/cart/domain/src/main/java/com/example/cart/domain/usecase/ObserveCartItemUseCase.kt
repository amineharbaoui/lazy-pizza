package com.example.cart.domain.usecase

import com.example.cart.domain.model.CartItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveCartItemUseCase @Inject constructor(private val observeCart: ObserveCartUseCase) {
    operator fun invoke(lineId: String): Flow<CartItem?> = observeCart().map { cart ->
        cart.items.find { it.lineId == lineId }
    }
}
