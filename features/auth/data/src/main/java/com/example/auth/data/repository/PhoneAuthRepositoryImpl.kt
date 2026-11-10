package com.example.auth.data.repository

import com.example.auth.data.datasource.PhoneAuthRemoteDataSource
import com.example.auth.domain.model.AuthUser
import com.example.auth.domain.repository.PhoneAuthRepository
import javax.inject.Inject

class PhoneAuthRepositoryImpl @Inject constructor(
    private val phoneAuthRemoteDataSource: PhoneAuthRemoteDataSource,
) : PhoneAuthRepository {

    override suspend fun signInWithCode(
        verificationId: String,
        smsCode: String,
    ): Result<AuthUser> = try {
        val remoteUser = phoneAuthRemoteDataSource.signInWithCode(verificationId, smsCode)
        Result.success(
            AuthUser(
                uid = remoteUser.uid,
                phoneNumber = remoteUser.phoneNumber,
            ),
        )
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun signOut() {
        phoneAuthRemoteDataSource.signOut()
    }
}
