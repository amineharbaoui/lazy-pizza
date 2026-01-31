package com.example.data.repository

import com.example.data.datasource.PhoneAuthDataSource
import com.example.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val phoneAuthDataSource: PhoneAuthDataSource,
) : SessionRepository {

    override val userIdFlow: Flow<String?>
        get() = phoneAuthDataSource.userIdFlow

    override val isSignedIn: Flow<Boolean>
        get() = phoneAuthDataSource.isSignedIn

    override suspend fun currentUserUid(): String? = phoneAuthDataSource.getCurrentUser()?.uid
}
