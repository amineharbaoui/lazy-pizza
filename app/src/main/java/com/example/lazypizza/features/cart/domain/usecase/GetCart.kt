package com.example.lazypizza.features.cart.domain.usecase

import com.example.lazypizza.features.cart.domain.model.Cart
import com.example.lazypizza.features.cart.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCart @Inject constructor(
    private val repository: CartRepository,
) {
    operator fun invoke(): Flow<Cart> = repository.cart
}