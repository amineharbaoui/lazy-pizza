package com.example.domain.repository

import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    val userIdFlow: Flow<String?>
    val isSignedIn: Flow<Boolean>
    suspend fun currentUserUid(): String?
}
