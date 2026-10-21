package com.example.cart.domain.usecase

import com.example.auth.domain.repository.SessionRepository
import com.example.cart.domain.model.Cart
import com.example.cart.domain.repository.CartRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveCartUseCase @Inject constructor(
    private val repository: CartRepository,
    private val sessionRepository: SessionRepository,
) {
    operator fun invoke(): Flow<Cart> = sessionRepository.observeCartOwner()
        .flatMapLatest { owner -> repository.observeCart(owner) }
}
