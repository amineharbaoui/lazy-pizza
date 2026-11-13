package com.example.auth.data.datasource

import com.example.auth.data.model.RemoteUser
import com.example.auth.domain.model.VerificationCodeStatus
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

internal interface PhoneAuthRemoteDataSource {
    val userIdFlow: Flow<String?>
    val isSignedIn: Flow<Boolean>
    fun getCurrentUser(): RemoteUser?
    fun signOut()
    fun sendVerificationCode(
        phoneNumber: String,
        timeout: Duration,
    ): Flow<VerificationCodeStatus>
    suspend fun signInWithCode(
        verificationId: String,
        smsCode: String,
    ): RemoteUser
}
