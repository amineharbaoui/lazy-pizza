package com.example.domain.usecase

import com.example.domain.model.Cart
import com.example.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCartUseCase @Inject constructor(private val repository: CartRepository) {
    operator fun invoke(): Flow<Cart> = repository.observeCart()
}
