package com.example.auth.domain.repository

import com.example.auth.domain.model.AuthUser
import com.example.auth.domain.model.VerificationCodeStatus
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface PhoneAuthRepository {
    fun sendVerificationCode(
        phoneNumber: String,
        timeout: Duration,
    ): Flow<VerificationCodeStatus>

    suspend fun signInWithCode(
        verificationId: String,
        smsCode: String,
    ): Result<AuthUser>

    suspend fun signOut()
}
