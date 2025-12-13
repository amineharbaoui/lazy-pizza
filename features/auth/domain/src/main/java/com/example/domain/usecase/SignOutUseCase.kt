package com.example.domain.usecase

import com.example.domain.repository.CartRepository
import com.example.domain.repository.PhoneAuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val phoneAuthRepository: PhoneAuthRepository,
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke() {
        phoneAuthRepository.currentUser()?.uid?.let { userId -> cartRepository.clearUserCart(userId) }
        phoneAuthRepository.signOut()
    }
}
