package com.example.auth.domain.repository

import com.example.auth.domain.model.AuthUser

interface PhoneAuthRepository {
    suspend fun signInWithCode(
        verificationId: String,
        smsCode: String,
    ): Result<AuthUser>

    suspend fun signOut()
}
