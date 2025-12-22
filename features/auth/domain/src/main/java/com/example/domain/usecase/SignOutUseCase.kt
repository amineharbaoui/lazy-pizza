package com.example.domain.usecase

import com.example.domain.repository.PhoneAuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val phoneAuthRepository: PhoneAuthRepository,
) {
    suspend operator fun invoke() {
        phoneAuthRepository.signOut()
    }
}
