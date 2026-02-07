package com.example.cart.domain.usecase

import com.example.cart.domain.model.CartItem
import com.example.cart.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveCartItemUseCase @Inject constructor(private val repository: CartRepository) {
    operator fun invoke(lineId: String): Flow<CartItem?> {
        return repository.observeCart().map { cart ->
            cart.items.find { it.lineId == lineId }
        }
    }
}
