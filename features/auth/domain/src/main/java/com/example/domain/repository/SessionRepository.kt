package com.example.domain.repository

import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    val userIdFlow: Flow<String?>
    suspend fun currentUserId(): String?
}
