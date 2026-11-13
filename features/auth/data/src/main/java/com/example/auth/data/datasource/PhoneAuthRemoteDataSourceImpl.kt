package com.example.auth.data.datasource

import com.example.auth.data.model.RemoteUser
import com.example.auth.domain.model.VerificationCodeStatus
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.time.Duration

internal class PhoneAuthRemoteDataSourceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val resumedActivityTracker: ResumedActivityTracker,
) : PhoneAuthRemoteDataSource {

    // TODO Unify userIdFlow and isSignedIn
    override val userIdFlow: Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.uid)
        }
        firebaseAuth.addAuthStateListener(listener)
        trySend(firebaseAuth.currentUser?.uid)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override val isSignedIn: Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser != null)
        }
        firebaseAuth.addAuthStateListener(listener)
        trySend(firebaseAuth.currentUser != null)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override fun getCurrentUser(): RemoteUser? {
        val user = firebaseAuth.currentUser ?: return null
        return RemoteUser(
            uid = user.uid,
            phoneNumber = user.phoneNumber,
        )
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }

    override fun sendVerificationCode(
        phoneNumber: String,
        timeout: Duration,
    ): Flow<VerificationCodeStatus> = callbackFlow {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            // Auto sign-in isn't supported: the user types the code they received.
            override fun onVerificationCompleted(credential: PhoneAuthCredential) = Unit

            override fun onVerificationFailed(e: FirebaseException) {
                close(e)
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                trySend(VerificationCodeStatus.Sent(verificationId))
            }

            override fun onCodeAutoRetrievalTimeOut(verificationId: String) {
                trySend(VerificationCodeStatus.TimedOut)
                close()
            }
        }
        val activity = checkNotNull(resumedActivityTracker.resumedActivity) {
            "Phone verification needs a resumed Activity"
        }
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(timeout.inWholeSeconds, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        awaitClose()
    }

    override suspend fun signInWithCode(
        verificationId: String,
        smsCode: String,
    ): RemoteUser {
        val credential = PhoneAuthProvider.getCredential(verificationId, smsCode)
        val firebaseUser = signInWithCredential(credential)
        return RemoteUser(
            uid = firebaseUser.uid,
            phoneNumber = firebaseUser.phoneNumber,
        )
    }

    private suspend fun signInWithCredential(credential: PhoneAuthCredential) = suspendCancellableCoroutine { continuationHandler ->
        firebaseAuth
            .signInWithCredential(credential)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user != null) {
                    continuationHandler.resume(user)
                } else {
                    continuationHandler.resumeWithException(
                        IllegalStateException("FirebaseUser is null after signInWithCredential"),
                    )
                }
            }
            .addOnFailureListener { e ->
                if (continuationHandler.isActive) continuationHandler.resumeWithException(e)
            }
    }
}
