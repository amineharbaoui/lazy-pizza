package com.example.cart.domain.usecase

import com.example.cart.domain.repository.CartRepository
import javax.inject.Inject

class TransferGuestCartToUserUseCase @Inject constructor(private val repository: CartRepository) {
    suspend operator fun invoke(userId: String) = repository.transferGuestCartToUser(userId)
}
