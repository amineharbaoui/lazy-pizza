package com.example.domaine.usecase

import com.example.domaine.model.Cart
import com.example.domaine.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCartUseCase @Inject constructor(private val repository: CartRepository) {
    operator fun invoke(): Flow<Cart> = repository.observeCart()
}
