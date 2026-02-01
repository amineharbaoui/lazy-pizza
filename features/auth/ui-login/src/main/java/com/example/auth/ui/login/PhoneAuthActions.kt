package com.example.auth.ui.login

data class PhoneAuthActions(
    val onPhoneChange: (String) -> Unit,
    val onFullPhoneNumberChange: (String) -> Unit,
    val onValidityChange: (Boolean) -> Unit,
    val onCountryIsoChanged: (String) -> Unit,
    val onContinue: () -> Unit,
    val onCodeChange: (String) -> Unit,
    val onConfirm: () -> Unit,
    val onResend: () -> Unit,
    val onSkip: () -> Unit,
    val onEditPhone: () -> Unit,
)
