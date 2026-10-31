package com.example.auth.data.datasource

import com.example.auth.data.model.RemoteUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** Signed-in uid is scripted with [signedInUid]; null means signed out. */
class FakePhoneAuthDataSource : PhoneAuthDataSource {

    private val uid = MutableStateFlow<String?>(null)

    var signedInUid: String?
        get() = uid.value
        set(value) {
            uid.value = value
        }

    override val userIdFlow: Flow<String?> = uid

    override val isSignedIn: Flow<Boolean> = uid.map { it != null }

    override fun getCurrentUser(): RemoteUser? = uid.value?.let { RemoteUser(uid = it, phoneNumber = null) }

    override fun signOut() {
        uid.value = null
    }

    override suspend fun signInWithCode(
        verificationId: String,
        smsCode: String,
    ): RemoteUser = checkNotNull(getCurrentUser()) { "Set signedInUid before signing in" }
}
