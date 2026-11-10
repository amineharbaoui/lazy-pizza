package com.example.auth.data

import com.example.auth.data.datasource.FakePhoneAuthRemoteDataSource
import com.example.auth.data.model.RemoteUser
import com.example.auth.data.repository.SessionRepositoryImpl
import com.example.auth.domain.repository.SessionRepository

class AuthTestData {

    private val dataSource = FakePhoneAuthRemoteDataSource()

    val sessionRepository: SessionRepository = SessionRepositoryImpl(dataSource)

    fun givenSignedIn(uid: String) {
        dataSource.user.value = RemoteUser(uid = uid, phoneNumber = null)
    }

    fun givenSignedOut() {
        dataSource.user.value = null
    }
}
