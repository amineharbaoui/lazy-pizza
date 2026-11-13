package com.example.auth.ui.login

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.designsystem.components.DsButton
import com.example.core.designsystem.components.DsTextField
import com.example.core.designsystem.components.DsTopBar
import com.example.core.designsystem.theme.AppColors
import com.example.core.designsystem.theme.AppTypography
import com.example.core.designsystem.theme.LazyPizzaThemePreview
import com.example.core.designsystem.utils.PreviewPhoneTablet
import com.example.core.designsystem.R as DS_R

@Composable
fun PhoneAuthScreen(
    innerPadding: PaddingValues,
    onAuthSuccess: () -> Unit,
    onNavigateToMenuScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PhoneAuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    PhoneAuthScreenContent(
        innerPadding = innerPadding,
        uiState = uiState,
        modifier = modifier,
        actions = PhoneAuthActions(
            onPhoneChange = viewModel::updatePhone,
            onFullPhoneNumberChange = viewModel::updateFullPhone,
            onValidityChange = viewModel::updatePhoneValidity,
            onCountryIsoChanged = viewModel::updateCountryIso,
            onContinue = viewModel::verifyPhone,
            onCodeChange = viewModel::updateCode,
            onConfirm = viewModel::confirmCode,
            onResend = viewModel::resendCode,
            onSkip = viewModel::skipAuth,
            onEditPhone = viewModel::editPhone,
        ),
    )
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                PhoneAuthEvent.SkipAuth -> onNavigateToMenuScreen()
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
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_phone_input"),
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
            text = stringResource(id = DS_R.string.continue_text),
            enabled = state.continueEnabled,
            onClick = actions.onContinue,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(6.dp))

        DsButton.Text(
            text = stringResource(id = DS_R.string.skip),
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
            text = stringResource(id = DS_R.string.change_number),
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
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_code_input"),
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
            text = stringResource(id = DS_R.string.confirm),
            enabled = state.confirmEnabled,
            onClick = actions.onConfirm,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(6.dp))

        DsButton.Text(
            text = stringResource(id = DS_R.string.skip),
            onClick = actions.onSkip,
            enabled = state.skipEnabled,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(8.dp))

        if (state.resendEnabled) {
            DsButton.Text(
                text = stringResource(id = DS_R.string.resend_code),
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

@PreviewPhoneTablet
@Composable
private fun EnterPhonePreview() {
    PhoneAuthPreview(uiState = previewEnterPhoneState)
}

@PreviewPhoneTablet
@Composable
private fun EnterPhoneValidPreview() {
    PhoneAuthPreview(uiState = previewEnterPhoneState.copy(phoneNumber = PREVIEW_PHONE_LOCAL, isPhoneValid = true))
}

@PreviewPhoneTablet
@Composable
private fun EnterPhoneLoadingPreview() {
    PhoneAuthPreview(uiState = previewEnterPhoneState.copy(phoneNumber = PREVIEW_PHONE_LOCAL, isPhoneValid = true, isLoading = true))
}

@PreviewPhoneTablet
@Composable
private fun EnterPhoneErrorPreview() {
    PhoneAuthPreview(
        uiState = previewEnterPhoneState.copy(
            phoneNumber = PREVIEW_PHONE_LOCAL,
            isPhoneValid = true,
            errorMessage = "Something went wrong. Please try again.",
        ),
    )
}

@PreviewPhoneTablet
@Composable
private fun EnterCodePreview() {
    PhoneAuthPreview(uiState = previewEnterCodeState)
}

@PreviewPhoneTablet
@Composable
private fun EnterCodeResendPreview() {
    PhoneAuthPreview(uiState = previewEnterCodeState.copy(canResend = true, secondsRemaining = 0))
}

@PreviewPhoneTablet
@Composable
private fun EnterCodeLoadingPreview() {
    PhoneAuthPreview(uiState = previewEnterCodeState.copy(code = PREVIEW_CODE, isLoading = true))
}

@PreviewPhoneTablet
@Composable
private fun EnterCodeErrorPreview() {
    PhoneAuthPreview(uiState = previewEnterCodeState.copy(code = PREVIEW_CODE, errorMessage = "Incorrect code. Please try again."))
}

@Composable
private fun PhoneAuthPreview(uiState: PhoneAuthUiState) {
    LazyPizzaThemePreview {
        PhoneAuthScreenContent(
            innerPadding = PaddingValues(),
            uiState = uiState,
            actions = PhoneAuthActions(
                onPhoneChange = {},
                onFullPhoneNumberChange = {},
                onValidityChange = {},
                onCountryIsoChanged = {},
                onContinue = {},
                onCodeChange = {},
                onConfirm = {},
                onResend = {},
                onSkip = {},
                onEditPhone = {},
            ),
        )
    }
}

private const val PREVIEW_PHONE_LOCAL = "2025550123"
private const val PREVIEW_FULL_PHONE = "+12025550123"
private const val PREVIEW_COUNTRY_ISO = "US"
private const val PREVIEW_CODE = "123456"

private val previewEnterPhoneState = PhoneAuthUiState.EnterPhone(
    phoneNumber = "",
    fullPhoneNumber = "",
    isPhoneValid = false,
    selectedCountryIso = PREVIEW_COUNTRY_ISO,
    isLoading = false,
    titleRes = R.string.auth_screen_title,
    subtitleRes = R.string.auth_screen_subtitle_enter_phone_number,
)

private val previewEnterCodeState = PhoneAuthUiState.EnterCode(
    phoneNumber = PREVIEW_FULL_PHONE,
    fullPhoneNumber = PREVIEW_FULL_PHONE,
    phoneLocal = PREVIEW_PHONE_LOCAL,
    countryIso = PREVIEW_COUNTRY_ISO,
    code = "",
    isLoading = false,
    canResend = false,
    secondsRemaining = 45,
    titleRes = R.string.auth_screen_title,
    subtitleRes = R.string.auth_screen_subtitle_enter_code,
)
