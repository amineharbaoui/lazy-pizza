package com.example.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.designsystem.R
import com.example.designsystem.components.DsButton
import com.example.designsystem.components.DsTextField
import com.example.designsystem.components.DsTopBar
import com.example.designsystem.theme.AppColors
import com.example.designsystem.theme.AppTypography
import com.google.firebase.auth.FirebaseAuth

@Composable
fun PhoneAuthScreen(
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: PhoneAuthViewModel = hiltViewModel(),
    onAuthSuccess: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalActivity.current ?: return
    PhoneAuthScreenContent(
        innerPadding = innerPadding,
        uiState = uiState,
        modifier = modifier,
        actions = PhoneAuthActions(
            onPhoneChange = viewModel::onPhoneChanged,
            onFullPhoneNumberChange = viewModel::onFullPhoneChanged,
            onValidityChange = viewModel::onPhoneValidityChanged,
            onCountryIsoChanged = viewModel::onCountryIsoChanged,
            onContinue = viewModel::onContinueWithPhoneClick,
            onCodeChange = viewModel::onCodeChanged,
            onConfirm = viewModel::onConfirmClick,
            onResend = viewModel::onResendCodeClick,
            onSkip = viewModel::onSkipClick,
            onEditPhone = viewModel::onEditPhoneClick,
        ),
    )
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is PhoneAuthEvent.StartPhoneVerification -> {
                    PhoneVerificationManager(FirebaseAuth.getInstance())
                        .startVerification(
                            activity = activity,
                            phoneNumber = event.phoneNumber,
                            onCodeSent = viewModel::onCodeSent,
                            onVerificationFailed = viewModel::onVerificationFailed,
                        )
                }

                is PhoneAuthEvent.ResendCode -> {
                    PhoneVerificationManager(FirebaseAuth.getInstance())
                        .startVerification(
                            activity = activity,
                            phoneNumber = event.phoneNumber,
                            onCodeSent = viewModel::onCodeSent,
                            onVerificationFailed = viewModel::onVerificationFailed,
                        )
                }

                PhoneAuthEvent.SkipAuth -> Unit
                PhoneAuthEvent.AuthCompleted -> onAuthSuccess()
            }
        }
    }
}

@Composable
fun PhoneAuthScreenContent(
    innerPadding: PaddingValues,
    uiState: PhoneAuthUiState,
    actions: PhoneAuthActions,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding),
        verticalArrangement = Arrangement.Top,
    ) {
        DsTopBar.Secondary(title = null)
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(uiState.titleRes),
            style = AppTypography.Title2SemiBold,
            color = AppColors.TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(uiState.subtitleRes),
            style = AppTypography.Body3Regular,
            color = AppColors.TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(24.dp))

        when (uiState) {
            is PhoneAuthUiState.EnterPhone -> PhoneAuthContent(
                state = uiState,
                actions = actions,
            )

            is PhoneAuthUiState.EnterCode -> CodeAuthContent(
                state = uiState,
                actions = actions,
            )
        }
    }
}

@Composable
private fun PhoneAuthContent(
    state: PhoneAuthUiState.EnterPhone,
    actions: PhoneAuthActions,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DsTextField.PhoneNumber(
            modifier = Modifier.fillMaxWidth(),
            phoneNumber = state.phoneNumber,
            onPhoneNumberChange = actions.onPhoneChange,
            onFullPhoneNumberChange = actions.onFullPhoneNumberChange,
            onValidityChange = actions.onValidityChange,
            initialCountryIso = state.selectedCountryIso,
            onCountryIsoChanged = actions.onCountryIsoChanged,
            isError = state.showError,
            enabled = !state.isLoading,
        )

        if (state.showError) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = state.errorMessage.orEmpty(),
                color = AppColors.Error,
                style = AppTypography.Body4Regular,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(12.dp))

        DsButton.Filled(
            text = stringResource(id = R.string.continue_text),
            enabled = state.continueEnabled,
            onClick = actions.onContinue,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(6.dp))

        DsButton.Text(
            text = stringResource(id = R.string.skip),
            onClick = actions.onSkip,
            enabled = state.skipEnabled,
            modifier = Modifier.fillMaxWidth(),
        )

        if (state.isLoading) {
            Spacer(Modifier.height(12.dp))
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun CodeAuthContent(
    state: PhoneAuthUiState.EnterCode,
    actions: PhoneAuthActions,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DsTextField.PhoneNumber(
            modifier = Modifier.fillMaxWidth(),
            phoneNumber = state.phoneLocal,
            onPhoneNumberChange = {},
            onFullPhoneNumberChange = {},
            onValidityChange = {},
            initialCountryIso = state.countryIso,
            isError = false,
            enabled = false,
        )
        DsButton.Text(
            text = stringResource(id = R.string.change_number),
            onClick = actions.onEditPhone,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))

        DsTextField.OtpCode(
            value = state.code,
            onValueChange = actions.onCodeChange,
            length = 6,
            isError = state.showError,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth(),
        )

        if (state.showError) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = state.errorMessage.orEmpty(),
                color = AppColors.Error,
                style = AppTypography.Body4Regular,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(12.dp))

        DsButton.Filled(
            text = stringResource(id = R.string.confirm),
            enabled = state.confirmEnabled,
            onClick = actions.onConfirm,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(6.dp))

        DsButton.Text(
            text = stringResource(id = R.string.skip),
            onClick = actions.onSkip,
            enabled = state.skipEnabled,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(8.dp))

        if (state.resendEnabled) {
            DsButton.Text(
                text = stringResource(id = R.string.resend_code),
                onClick = actions.onResend,
                enabled = state.resendEnabled,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            Text(
                text = state.resendTimerText.orEmpty(),
                style = AppTypography.Body4Regular,
                color = AppColors.TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (state.isLoading) {
            Spacer(Modifier.height(12.dp))
            CircularProgressIndicator()
        }
    }
}
