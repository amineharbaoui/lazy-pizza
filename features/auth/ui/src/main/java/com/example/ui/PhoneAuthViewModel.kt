package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.SignInWithSmsCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhoneAuthViewModel @Inject constructor(
    private val signInWithSmsCode: SignInWithSmsCodeUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<PhoneAuthUiState>(
        PhoneAuthUiState.EnterPhone(
            phoneNumber = "",
            fullPhoneNumber = "",
            isPhoneValid = false,
            isLoading = false,
            errorMessage = null,
        ),
    )
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PhoneAuthEvent>()
    val events = _events.asSharedFlow()

    private var currentVerificationId: String? = null

    fun onPhoneChanged(phone: String) {
        val normalized = phone.trim()
        val current = _uiState.value
        if (current is PhoneAuthUiState.EnterPhone) {
            _uiState.value = current.copy(
                phoneNumber = normalized,
                errorMessage = null,
            )
        }
    }

    fun onFullPhoneChanged(fullPhone: String) {
        val current = _uiState.value
        if (current is PhoneAuthUiState.EnterPhone) {
            _uiState.value = current.copy(
                fullPhoneNumber = fullPhone,
                errorMessage = null,
            )
        }
    }

    fun onPhoneValidityChanged(isValid: Boolean) {
        val current = _uiState.value
        if (current is PhoneAuthUiState.EnterPhone) {
            _uiState.value = current.copy(
                isPhoneValid = isValid,
                errorMessage = null,
            )
        }
    }

    fun onContinueWithPhoneClick() {
        val current = _uiState.value as? PhoneAuthUiState.EnterPhone ?: return
        _uiState.value = current.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            _events.emit(PhoneAuthEvent.StartPhoneVerification(current.fullPhoneNumber))
        }
    }

    fun onSkipClick() {
        viewModelScope.launch { _events.emit(PhoneAuthEvent.SkipAuth) }
    }

    fun onCodeSent(verificationId: String) {
        currentVerificationId = verificationId
        val phone = (uiState.value as? PhoneAuthUiState.EnterPhone)?.fullPhoneNumber ?: return

        _uiState.value = PhoneAuthUiState.EnterCode(
            phone = phone,
            code = "",
            isLoading = false,
            errorMessage = null,
            canResend = false,
        )
    }

    fun onVerificationFailed(message: String?) {
        val text = message ?: "Something went wrong. Please try again."
        when (val current = _uiState.value) {
            is PhoneAuthUiState.EnterPhone ->
                _uiState.value = current.copy(isLoading = false, errorMessage = text)

            is PhoneAuthUiState.EnterCode ->
                _uiState.value = current.copy(isLoading = false, errorMessage = text)

            else -> Unit
        }
    }

    // --- code step --------------------------------------------------------

    fun onCodeChanged(code: String) {
        val digits = code.filter { it.isDigit() }.take(6)
        val current = _uiState.value as? PhoneAuthUiState.EnterCode ?: return
        _uiState.value = current.copy(code = digits, errorMessage = null)
    }

    fun onConfirmClick() {
        val current = _uiState.value as? PhoneAuthUiState.EnterCode ?: return
        val verificationId = currentVerificationId ?: return
        if (current.code.length < 6 || current.isLoading) return

        _uiState.value = current.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val result = signInWithSmsCode(verificationId, current.code)
            result
                .onSuccess {
                    resetState()
                    _events.emit(PhoneAuthEvent.AuthCompleted)
                }
                .onFailure { e ->
                    _uiState.value = current.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Invalid code. Try again.",
                    )
                }
        }
    }

    fun onResendCodeClick() {
        val current = _uiState.value as? PhoneAuthUiState.EnterCode ?: return
        viewModelScope.launch { _events.emit(PhoneAuthEvent.ResendCode(current.phone)) }
    }

    private fun resetState() {
        currentVerificationId = null
        _uiState.value = PhoneAuthUiState.EnterPhone(
            phoneNumber = "",
            fullPhoneNumber = "",
            isPhoneValid = false,
            isLoading = false,
            errorMessage = null,
        )
    }

    private fun validatePhone(phone: String) = phone.length >= 8
}
