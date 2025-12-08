package com.example.ui

sealed interface PhoneAuthUiState {

    data object Idle : PhoneAuthUiState

    data class EnterPhone(
        val phoneNumber: String,
        val fullPhoneNumber: String,
        val isPhoneValid: Boolean,
        val errorMessage: String? = null,
        val isLoading: Boolean,
    ) : PhoneAuthUiState

    data class EnterCode(
        val phone: String,
        val code: String,
        val errorMessage: String? = null,
        val isLoading: Boolean,
        val canResend: Boolean,
    ) : PhoneAuthUiState
}
