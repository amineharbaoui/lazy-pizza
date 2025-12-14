package com.example.uilogin

sealed interface PhoneAuthEvent {
    data class StartPhoneVerification(val phoneNumber: String) : PhoneAuthEvent
    data class ResendCode(val phoneNumber: String) : PhoneAuthEvent
    data object SkipAuth : PhoneAuthEvent
    data object AuthCompleted : PhoneAuthEvent
}
