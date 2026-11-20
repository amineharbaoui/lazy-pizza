package com.example.auth.data.repository

import com.example.auth.data.datasource.PhoneAuthRemoteDataSource
import com.example.auth.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class SessionRepositoryImpl @Inject constructor(
    private val phoneAuthRemoteDataSource: PhoneAuthRemoteDataSource,
) : SessionRepository {

    override val userIdFlow: Flow<String?>
        get() = phoneAuthRemoteDataSource.userIdFlow

    override val isSignedIn: Flow<Boolean>
        get() = phoneAuthRemoteDataSource.userIdFlow.map { it != null }

    override suspend fun currentUserUid(): String? = phoneAuthRemoteDataSource.getCurrentUser()?.uid
}
