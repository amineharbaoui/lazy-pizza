package com.example.data.repository

import com.example.domain.repository.PhoneAuthRepository
import com.example.domain.repository.SessionRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val phoneAuthRepository: PhoneAuthRepository,
    private val firebaseAuth: FirebaseAuth,
) : SessionRepository {

    override val userIdFlow: Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.uid)
        }
        firebaseAuth.addAuthStateListener(listener)
        trySend(firebaseAuth.currentUser?.uid)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun currentUserId(): String? = phoneAuthRepository.currentUser()?.uid
}
