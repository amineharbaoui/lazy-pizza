package com.example.domain.usecase

import com.example.domain.model.AuthUser
import com.example.domain.repository.PhoneAuthRepository

class SignInWithSmsCodeUseCase(
    private val repository: PhoneAuthRepository,
) {
    suspend operator fun invoke(
        verificationId: String,
        smsCode: String,
    ): Result<AuthUser> = repository.signInWithCode(verificationId, smsCode)
}
