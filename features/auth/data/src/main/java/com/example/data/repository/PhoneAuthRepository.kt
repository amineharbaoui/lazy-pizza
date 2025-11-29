package com.example.data.repository

import com.example.data.datasource.PhoneAuthDataSource
import com.example.domain.model.AuthUser
import com.example.domain.repository.PhoneAuthRepository
import javax.inject.Inject

class FirebasePhoneAuthRepository @Inject constructor(
    private val remoteDataSource: PhoneAuthDataSource,
) : PhoneAuthRepository {

    override suspend fun signInWithCode(
        verificationId: String,
        smsCode: String,
    ): Result<AuthUser> = try {
        val remoteUser = remoteDataSource.signInWithCode(verificationId, smsCode)
        Result.success(
            AuthUser(
                uid = remoteUser.uid,
                phoneNumber = remoteUser.phoneNumber,
            ),
        )
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun currentUser(): AuthUser? {
        val remoteUser = remoteDataSource.getCurrentUser() ?: return null
        return AuthUser(
            uid = remoteUser.uid,
            phoneNumber = remoteUser.phoneNumber,
        )
    }

    override suspend fun signOut() {
        remoteDataSource.signOut()
    }
}
