package com.example.ui

sealed interface PhoneAuthEvent {
    data class StartPhoneVerification(val phone: String) : PhoneAuthEvent
    data class ResendCode(val phone: String) : PhoneAuthEvent
    data object SkipAuth : PhoneAuthEvent
    data object AuthCompleted : PhoneAuthEvent
}
