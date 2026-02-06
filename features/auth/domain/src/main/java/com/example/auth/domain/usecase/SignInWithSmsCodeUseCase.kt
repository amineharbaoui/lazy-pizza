package com.example.auth.domain.usecase

import com.example.auth.domain.model.AuthUser
import com.example.auth.domain.repository.PhoneAuthRepository
import javax.inject.Inject

class SignInWithSmsCodeUseCase @Inject constructor(
    private val repository: PhoneAuthRepository,
) {
    suspend operator fun invoke(
        verificationId: String,
        smsCode: String,
    ): Result<AuthUser> = repository.signInWithCode(verificationId, smsCode)
}
