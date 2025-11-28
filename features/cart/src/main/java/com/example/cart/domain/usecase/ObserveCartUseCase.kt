package com.example.cart.domain.usecase

import com.example.cart.domain.model.Cart
import com.example.cart.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCartUseCase @Inject constructor(
    private val repository: CartRepository,
) {
    operator fun invoke(): Flow<Cart> = repository.observeCart()
}