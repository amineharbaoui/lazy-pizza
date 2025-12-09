package com.example.ui

import androidx.annotation.StringRes

sealed interface PhoneAuthUiState {
    @get:StringRes
    val titleRes: Int

    @get:StringRes
    val subtitleRes: Int

    data class EnterPhone(
        val phoneNumber: String,
        val fullPhoneNumber: String,
        val isPhoneValid: Boolean,
        val selectedCountryIso: String? = null,
        val errorMessage: String? = null,
        val isLoading: Boolean,
        override val titleRes: Int,
        override val subtitleRes: Int,
    ) : PhoneAuthUiState {
        val continueEnabled: Boolean get() = isPhoneValid && !isLoading
        val skipEnabled: Boolean get() = !isLoading
        val showError: Boolean get() = errorMessage != null
    }

    data class EnterCode(
        val phoneNumber: String,
        val fullPhoneNumber: String,
        val phoneLocal: String,
        val countryIso: String?,
        val code: String,
        val errorMessage: String? = null,
        val isLoading: Boolean,
        val canResend: Boolean,
        val secondsRemaining: Int,
        override val titleRes: Int,
        override val subtitleRes: Int,
    ) : PhoneAuthUiState {
        val confirmEnabled: Boolean get() = code.length == 6 && !isLoading
        val skipEnabled: Boolean get() = !isLoading
        val resendEnabled: Boolean get() = canResend && !isLoading
        val showError: Boolean get() = errorMessage != null

        val resendTimerText: String?
            get() = if (!canResend) {
                val mm = secondsRemaining / 60
                val ss = secondsRemaining % 60
                String.format("You can request a new code in %02d:%02d", mm, ss)
            } else {
                null
            }
    }
}
