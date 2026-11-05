package com.example.auth.data.datasource

import com.example.auth.data.model.RemoteUser
import kotlinx.coroutines.flow.Flow

interface PhoneAuthDataSource {
    val userIdFlow: Flow<String?>
    val isSignedIn: Flow<Boolean>
    fun getCurrentUser(): RemoteUser?
    fun signOut()
    suspend fun signInWithCode(
        verificationId: String,
        smsCode: String,
    ): RemoteUser
}
