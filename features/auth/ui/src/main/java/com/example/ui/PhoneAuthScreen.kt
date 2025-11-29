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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.designsystem.R
import com.example.designsystem.components.DsButton
import com.example.designsystem.components.DsTextField
import com.example.designsystem.components.DsTopBar
import com.example.designsystem.theme.AppColors
import com.example.designsystem.theme.AppTypography
import com.example.designsystem.theme.LazyPizzaThemePreview
import com.google.firebase.auth.FirebaseAuth

@Composable
fun PhoneAuthScreen(
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: PhoneAuthViewModel = hiltViewModel(),
    onAuthSuccess: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding),
        verticalArrangement = Arrangement.Top,
    ) {
        DsTopBar.Secondary(title = null)
        when (val state = uiState) {
            is PhoneAuthUiState.EnterPhone -> PhoneStep(
                state = state,
                onPhoneChange = viewModel::onPhoneChanged,
                onContinue = viewModel::onContinueWithPhoneClick,
                onSkip = viewModel::onSkipClick,
            )

            is PhoneAuthUiState.EnterCode -> CodeStep(
                state = state,
                onCodeChange = viewModel::onCodeChanged,
                onConfirm = viewModel::onConfirmClick,
                onResend = viewModel::onResendCodeClick,
            )

            PhoneAuthUiState.Idle -> Unit
        }
    }

    val activity = LocalActivity.current ?: return
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is PhoneAuthEvent.StartPhoneVerification -> {
                    PhoneVerificationManager(FirebaseAuth.getInstance())
                        .startVerification(
                            activity = activity,
                            phoneNumber = event.phone,
                            onCodeSent = viewModel::onCodeSent,
                            onVerificationFailed = { e -> viewModel.onVerificationFailed(e.message) },
                        )
                }

                is PhoneAuthEvent.ResendCode -> {
                    PhoneVerificationManager(FirebaseAuth.getInstance())
                        .startVerification(
                            activity = activity,
                            phoneNumber = event.phone,
                            onCodeSent = viewModel::onCodeSent,
                            onVerificationFailed = { e -> viewModel.onVerificationFailed(e.message) },
                        )
                }

                PhoneAuthEvent.SkipAuth -> Unit
                PhoneAuthEvent.AuthCompleted -> onAuthSuccess()
            }
        }
    }
}

@Composable
private fun PhoneStep(
    state: PhoneAuthUiState.EnterPhone,
    onPhoneChange: (String) -> Unit,
    onContinue: () -> Unit,
    onSkip: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Welcome to LazyPizza",
            style = AppTypography.Title2SemiBold,
            color = AppColors.TextPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Let’s begin your order",
            style = AppTypography.Body3Regular,
            color = AppColors.TextSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))

        DsTextField.Primary(
            value = state.phoneNumber,
            onValueChange = onPhoneChange,
            label = "Phone number",
            isError = state.errorMessage != null,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading,
        )

        state.errorMessage?.let {
            Spacer(Modifier.height(8.dp))
            Text(text = it, color = AppColors.Error, style = AppTypography.Body4Regular)
        }

        Spacer(Modifier.height(12.dp))
        DsButton.Filled(
            text = stringResource(id = R.string.continue_text),
            enabled = state.isPhoneValid && !state.isLoading,
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(6.dp))
        DsButton.Text(
            text = stringResource(id = R.string.skip),
            onClick = onSkip,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth(),
        )

        if (state.isLoading) {
            Spacer(Modifier.height(12.dp))
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun CodeStep(
    state: PhoneAuthUiState.EnterCode,
    onCodeChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onResend: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Welcome to LazyPizza",
            style = AppTypography.Title2SemiBold,
            color = AppColors.TextPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Enter SMS code",
            style = AppTypography.Body3Regular,
            color = AppColors.TextSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))

        // Simple single field limiting to 6 digits. Could be replaced with fancy OTP later.
        DsTextField.Primary(
            value = state.code,
            onValueChange = onCodeChange,
            label = "6-digit code",
            isError = state.errorMessage != null,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading,
        )

        state.errorMessage?.let {
            Spacer(Modifier.height(8.dp))
            Text(text = it, color = AppColors.Error, style = AppTypography.Body4Regular)
        }

        Spacer(Modifier.height(12.dp))
        DsButton.Filled(
            text = stringResource(id = R.string.confirm),
            enabled = state.code.length == 6 && !state.isLoading,
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(6.dp))
        DsButton.Text(
            text = stringResource(id = R.string.resend_code),
            onClick = onResend,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth(),
        )

        if (state.isLoading) {
            Spacer(Modifier.height(12.dp))
            CircularProgressIndicator()
        }
    }
}

@Preview
@Composable
private fun PhoneStepPreview() {
    LazyPizzaThemePreview {
        PhoneStep(
            state = PhoneAuthUiState.EnterPhone(
                phoneNumber = "",
                isPhoneValid = false,
                isLoading = false,
                errorMessage = null,
            ),
            onPhoneChange = {},
            onContinue = {},
            onSkip = {},
        )
    }
}

@Preview
@Composable
private fun CodeStepPreview() {
    LazyPizzaThemePreview {
        CodeStep(
            state = PhoneAuthUiState.EnterCode(
                phone = "+1 555 000 000",
                code = "123",
                isLoading = false,
                errorMessage = "Invalid code",
                canResend = true,
            ),
            onCodeChange = {},
            onConfirm = {},
            onResend = {},
        )
    }
}
