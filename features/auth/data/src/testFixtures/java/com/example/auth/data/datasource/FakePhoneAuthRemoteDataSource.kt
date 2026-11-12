package com.example.auth.data.datasource

import com.example.auth.data.model.RemoteUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

internal class FakePhoneAuthRemoteDataSource : PhoneAuthRemoteDataSource {

    val user = MutableStateFlow<RemoteUser?>(null)

    var failure: Exception? = null

    override val userIdFlow: Flow<String?> = user.map { it?.uid }

    override val isSignedIn: Flow<Boolean> = user.map { it != null }

    override fun getCurrentUser(): RemoteUser? = user.value

    override fun signOut() {
        user.value = null
    }

    override suspend fun signInWithCode(
        verificationId: String,
        smsCode: String,
    ): RemoteUser {
        failure?.let { throw it }
        return RemoteUser(uid = SIGNED_IN_UID, phoneNumber = null).also { user.value = it }
    }

    private companion object {
        const val SIGNED_IN_UID = "fake-uid"
    }
}
