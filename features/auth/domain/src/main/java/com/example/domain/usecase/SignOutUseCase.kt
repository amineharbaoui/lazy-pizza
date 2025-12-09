package com.example.domain.usecase

import com.example.domain.repository.PhoneAuthRepository

class SignOutUseCase(
    private val repository: PhoneAuthRepository,
) {
    suspend operator fun invoke() {
        repository.signOut()
    }
}
