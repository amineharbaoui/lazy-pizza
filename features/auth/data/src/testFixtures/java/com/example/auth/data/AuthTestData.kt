package com.example.auth.data

import com.example.auth.data.datasource.FakePhoneAuthRemoteDataSource
import com.example.auth.data.model.RemoteUser
import com.example.auth.data.repository.PhoneAuthRepositoryImpl
import com.example.auth.data.repository.SessionRepositoryImpl
import com.example.auth.domain.repository.PhoneAuthRepository
import com.example.auth.domain.repository.SessionRepository
import com.google.firebase.FirebaseException

class AuthTestData {

    private val dataSource = FakePhoneAuthRemoteDataSource()

    val sessionRepository: SessionRepository = SessionRepositoryImpl(dataSource)
    val phoneAuthRepository: PhoneAuthRepository = PhoneAuthRepositoryImpl(dataSource)

    fun givenSignedIn(uid: String) {
        dataSource.user.value = RemoteUser(uid = uid, phoneNumber = null)
    }

    fun givenSignedOut() {
        dataSource.user.value = null
    }

    fun givenSignInAs(uid: String) {
        dataSource.signInUid = uid
    }

    fun givenSignInFails(error: Exception = IllegalStateException(FORCED_FAILURE)) {
        dataSource.failure = error
    }

    fun givenVerificationCodeTimesOut() {
        dataSource.verificationCodeTimesOut = true
    }

    fun givenSendingVerificationCodeFails(message: String) {
        dataSource.sendVerificationCodeFailure = FirebaseException(message)
    }

    fun givenSendingVerificationCodeThrows() {
        dataSource.sendVerificationCodeFailure = IllegalStateException(FORCED_FAILURE)
    }

    fun verificationCodeRequests(): List<String> = dataSource.verificationCodeRequests.toList()

    private companion object {
        const val FORCED_FAILURE = "Forced failure"
    }
}
