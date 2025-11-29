package com.example.domain.repository

import com.example.domain.model.AuthUser

interface PhoneAuthRepository {
    suspend fun signInWithCode(
        verificationId: String,
        smsCode: String,
    ): Result<AuthUser>

    fun currentUser(): AuthUser?

    suspend fun signOut()
}
