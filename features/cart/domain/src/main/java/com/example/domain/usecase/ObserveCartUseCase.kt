package com.example.domain.usecase

import com.example.domain.model.Cart
import com.example.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow

class ObserveCartUseCase(private val repository: CartRepository) {
    operator fun invoke(): Flow<Cart> = repository.observeCart()
}
