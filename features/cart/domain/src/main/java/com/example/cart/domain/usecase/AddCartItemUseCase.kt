package com.example.cart.domain.usecase

import com.example.auth.domain.repository.SessionRepository
import com.example.cart.domain.model.CartItem
import com.example.cart.domain.repository.CartRepository
import javax.inject.Inject

class AddCartItemUseCase @Inject constructor(
    private val repository: CartRepository,
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke(item: CartItem) {
        repository.addItem(owner = sessionRepository.currentCartOwner(), item = item)
    }
}
