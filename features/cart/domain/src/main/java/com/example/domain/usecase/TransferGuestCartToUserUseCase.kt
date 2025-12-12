package com.example.domain.usecase

import com.example.domain.repository.CartRepository

class TransferGuestCartToUserUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(userId: String) = repository.transferGuestCartToUser(userId)
}
