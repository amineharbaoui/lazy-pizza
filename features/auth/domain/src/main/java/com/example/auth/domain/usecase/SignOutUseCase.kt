package com.example.auth.domain.usecase

import com.example.auth.domain.repository.PhoneAuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val phoneAuthRepository: PhoneAuthRepository,
) {
    suspend operator fun invoke() {
        phoneAuthRepository.signOut()
    }
}
