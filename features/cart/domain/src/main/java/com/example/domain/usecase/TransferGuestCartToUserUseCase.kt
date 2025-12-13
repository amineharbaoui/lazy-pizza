package com.example.domain.usecase

import com.example.domain.repository.CartRepository
import javax.inject.Inject

class TransferGuestCartToUserUseCase @Inject constructor(private val repository: CartRepository) {
    suspend operator fun invoke(userId: String) = repository.transferGuestCartToUser(userId)
}
