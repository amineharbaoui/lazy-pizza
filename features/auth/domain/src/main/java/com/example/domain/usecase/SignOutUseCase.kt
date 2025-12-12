package com.example.domain.usecase

import com.example.domain.repository.CartRepository
import com.example.domain.repository.PhoneAuthRepository

class SignOutUseCase(
    private val phoneAuthRepository: PhoneAuthRepository,
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke() {
        phoneAuthRepository.currentUser()?.uid?.let { userId -> cartRepository.clearUserCart(userId) }
        phoneAuthRepository.signOut()
    }
}
