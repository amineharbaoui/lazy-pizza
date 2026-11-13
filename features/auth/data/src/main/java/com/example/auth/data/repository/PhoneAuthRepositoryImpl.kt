package com.example.auth.data.repository

import com.example.auth.data.datasource.PhoneAuthRemoteDataSource
import com.example.auth.domain.model.AuthUser
import com.example.auth.domain.model.VerificationCodeStatus
import com.example.auth.domain.repository.PhoneAuthRepository
import com.google.firebase.FirebaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject
import kotlin.time.Duration

internal class PhoneAuthRepositoryImpl @Inject constructor(
    private val phoneAuthRemoteDataSource: PhoneAuthRemoteDataSource,
) : PhoneAuthRepository {

    override fun sendVerificationCode(
        phoneNumber: String,
        timeout: Duration,
    ): Flow<VerificationCodeStatus> = phoneAuthRemoteDataSource
        .sendVerificationCode(phoneNumber, timeout)
        .catch { e ->
            if (e !is FirebaseException) throw e
            emit(VerificationCodeStatus.Failed(e.message))
        }

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
