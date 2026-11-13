package com.example.auth.domain.model

sealed interface VerificationCodeStatus {
    data class Sent(val verificationId: String) : VerificationCodeStatus

    data object TimedOut : VerificationCodeStatus

    data class Failed(val message: String?) : VerificationCodeStatus
}
