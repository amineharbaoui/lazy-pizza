package com.example.domaine.usecase

import com.example.domaine.repository.CartRepository
import javax.inject.Inject

class TransferGuestCartToUserUseCase @Inject constructor(private val repository: CartRepository) {
    suspend operator fun invoke(userId: String) = repository.transferGuestCartToUser(userId)
}
