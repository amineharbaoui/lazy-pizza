package com.example.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.ui.R
import com.example.domain.usecase.SignInWithSmsCodeUseCase
import com.example.domain.usecase.TransferGuestCartToUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class PhoneAuthViewModel @Inject constructor(
    private val signInWithSmsCode: SignInWithSmsCodeUseCase,
    private val transferGuestCartToUserUseCase: TransferGuestCartToUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PhoneAuthUiState>(
        PhoneAuthUiState.EnterPhone(
            phoneNumber = "",
            fullPhoneNumber = "",
            isPhoneValid = false,
            selectedCountryIso = null,
            isLoading = false,
            errorMessage = null,
            titleRes = R.string.auth_screen_title,
            subtitleRes = R.string.auth_screen_subtitle_enter_phone_number,
        ),
    )
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PhoneAuthEvent>()
    val events = _events.asSharedFlow()

    private var currentVerificationId: String? = null
    private var resendTimerJob: Job? = null

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

    fun onCountryIsoChanged(iso: String) {
        val current = _uiState.value
        if (current is PhoneAuthUiState.EnterPhone) {
            _uiState.value = current.copy(
                selectedCountryIso = iso,
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
        if (!current.continueEnabled) return

        _uiState.value = current.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            _events.emit(PhoneAuthEvent.StartPhoneVerification(current.fullPhoneNumber))
        }
    }

    fun onEditPhoneClick() {
        val current = _uiState.value as? PhoneAuthUiState.EnterCode ?: return
        resendTimerJob?.cancel()
        resendTimerJob = null
        currentVerificationId = null

        _uiState.value = PhoneAuthUiState.EnterPhone(
            phoneNumber = current.phoneNumber,
            fullPhoneNumber = current.fullPhoneNumber,
            isPhoneValid = true,
            selectedCountryIso = current.countryIso,
            isLoading = false,
            errorMessage = null,
            titleRes = R.string.auth_screen_title,
            subtitleRes = R.string.auth_screen_subtitle_enter_phone_number,
        )
    }

    fun onSkipClick() {
        viewModelScope.launch { _events.emit(PhoneAuthEvent.SkipAuth) }
    }

    fun onCodeSent(verificationId: String) {
        currentVerificationId = verificationId
        val phoneState = (uiState.value as? PhoneAuthUiState.EnterPhone) ?: return
        val phone = phoneState.fullPhoneNumber

        _uiState.value = PhoneAuthUiState.EnterCode(
            phoneNumber = phone,
            fullPhoneNumber = phoneState.fullPhoneNumber,
            phoneLocal = phoneState.phoneNumber,
            countryIso = phoneState.selectedCountryIso,
            code = "",
            isLoading = false,
            errorMessage = null,
            canResend = false,
            secondsRemaining = PhoneVerificationManager.RESEND_TIMEOUT_SECONDS.toInt(),
            titleRes = R.string.auth_screen_title,
            subtitleRes = R.string.auth_screen_subtitle_enter_code,
        )
        startResendTimer()
    }

    fun onVerificationFailed(error: Throwable) {
        val text = error.message ?: "Something went wrong. Please try again."
        when (val current = _uiState.value) {
            is PhoneAuthUiState.EnterPhone -> _uiState.value = current.copy(isLoading = false, errorMessage = text)
            is PhoneAuthUiState.EnterCode -> _uiState.value = current.copy(isLoading = false, errorMessage = text)
        }
    }

    // --- code step --------------------------------------------------------

    fun onCodeChanged(code: String) {
        val digits = code.filter { it.isDigit() }.take(6)
        val current = _uiState.value as? PhoneAuthUiState.EnterCode ?: return
        _uiState.value = current.copy(code = digits, errorMessage = null)
    }

    fun onConfirmClick() {
        Log.d("TAG", "onConfirmClick() called")
        val current = _uiState.value as? PhoneAuthUiState.EnterCode ?: return
        val verificationId = currentVerificationId ?: return
        if (!current.confirmEnabled) return

        _uiState.value = current.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val result = signInWithSmsCode(verificationId, current.code)
            result
                .onSuccess { user ->
                    transferGuestCartToUserUseCase(user.uid)
                    resetState()
                    _events.emit(PhoneAuthEvent.AuthCompleted)
                }
                .onFailure { e ->
                    _uiState.value = current.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Incorrect code. Please try again.",
                    )
                }
        }
    }

    fun onResendCodeClick() {
        val current = _uiState.value as? PhoneAuthUiState.EnterCode ?: return
        if (!current.resendEnabled) return

        viewModelScope.launch {
            _events.emit(PhoneAuthEvent.ResendCode(current.phoneNumber))
        }

        _uiState.value = current.copy(
            canResend = false,
            secondsRemaining = PhoneVerificationManager.RESEND_TIMEOUT_SECONDS.toInt(),
            errorMessage = null,
        )
        startResendTimer()
    }

    private fun startResendTimer() {
        resendTimerJob?.cancel()
        resendTimerJob = viewModelScope.launch {
            while (true) {
                delay(1.seconds)
                val latest = _uiState.value as? PhoneAuthUiState.EnterCode ?: break
                val remaining = latest.secondsRemaining
                if (remaining <= 1) {
                    _uiState.value = latest.copy(secondsRemaining = 0, canResend = true)
                    break
                } else {
                    _uiState.value = latest.copy(secondsRemaining = remaining - 1)
                }
            }
        }
    }

    private fun resetState() {
        currentVerificationId = null
        resendTimerJob?.cancel()
        resendTimerJob = null

        _uiState.value = PhoneAuthUiState.EnterPhone(
            phoneNumber = "",
            fullPhoneNumber = "",
            isPhoneValid = false,
            selectedCountryIso = null,
            isLoading = false,
            errorMessage = null,
            titleRes = R.string.auth_screen_title,
            subtitleRes = R.string.auth_screen_subtitle_enter_phone_number,
        )
    }
}
