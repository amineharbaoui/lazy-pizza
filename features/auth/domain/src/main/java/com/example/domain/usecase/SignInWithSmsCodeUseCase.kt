package com.example.domain.usecase

import com.example.domain.model.AuthUser
import com.example.domain.repository.PhoneAuthRepository
import javax.inject.Inject

class SignInWithSmsCodeUseCase @Inject constructor(
    private val repository: PhoneAuthRepository,
) {
    suspend operator fun invoke(
        verificationId: String,
        smsCode: String,
    ): Result<AuthUser> = repository.signInWithCode(verificationId, smsCode)
}
