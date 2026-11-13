package com.example.auth.data.datasource

import com.example.auth.data.model.RemoteUser
import com.example.auth.domain.model.VerificationCodeStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlin.time.Duration

internal class FakePhoneAuthRemoteDataSource : PhoneAuthRemoteDataSource {

    val user = MutableStateFlow<RemoteUser?>(null)

    var failure: Exception? = null

    var signInUid: String = DEFAULT_SIGNED_IN_UID

    val verificationCodeRequests = mutableListOf<String>()

    var verificationCodeTimesOut: Boolean = false

    var sendVerificationCodeFailure: Exception? = null

    override val userIdFlow: Flow<String?> = user.map { it?.uid }

    override val isSignedIn: Flow<Boolean> = user.map { it != null }

    override fun getCurrentUser(): RemoteUser? = user.value

    override fun signOut() {
        user.value = null
    }

    override fun sendVerificationCode(
        phoneNumber: String,
        timeout: Duration,
    ): Flow<VerificationCodeStatus> = flow {
        verificationCodeRequests += phoneNumber
        sendVerificationCodeFailure?.let { throw it }
        emit(VerificationCodeStatus.Sent(VERIFICATION_ID))
        if (verificationCodeTimesOut) emit(VerificationCodeStatus.TimedOut)
    }

    override suspend fun signInWithCode(
        verificationId: String,
        smsCode: String,
    ): RemoteUser {
        failure?.let { throw it }
        return RemoteUser(uid = signInUid, phoneNumber = null).also { user.value = it }
    }

    private companion object {
        const val DEFAULT_SIGNED_IN_UID = "fake-uid"
        const val VERIFICATION_ID = "fake-verification-id"
    }
}
