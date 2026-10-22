package com.example.cart.domain.usecase

import com.example.auth.domain.repository.SessionRepository
import com.example.cart.domain.repository.CartRepository
import javax.inject.Inject

class RemoveCartItemUseCase @Inject constructor(
    private val repository: CartRepository,
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke(lineId: String) {
        repository.removeItem(owner = sessionRepository.currentCartOwner(), lineId = lineId)
    }
}
