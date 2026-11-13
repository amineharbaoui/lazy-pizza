package com.example.auth.ui.login

sealed interface PhoneAuthEvent {
    data object SkipAuth : PhoneAuthEvent
    data object AuthCompleted : PhoneAuthEvent
}
