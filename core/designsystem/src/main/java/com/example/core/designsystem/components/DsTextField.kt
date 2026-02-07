package com.example.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.core.designsystem.R
import com.example.core.designsystem.components.otp.OtpCodeInput
import com.example.core.designsystem.components.phonenumber.PhoneNumberField
import com.example.core.designsystem.theme.AppColors
import com.example.core.designsystem.theme.AppTypography
import com.example.core.designsystem.theme.LazyPizzaThemePreview

object DsTextField {

    @Composable
    fun Primary(
        value: String,
        onValueChange: (String) -> Unit,
        placeholder: String,
        modifier: Modifier = Modifier,
        isError: Boolean = false,
        enabled: Boolean = true,
        singleLine: Boolean = false,
        maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
        minLines: Int = 1,
    ) {
        val containerColor = if (value.isEmpty()) AppColors.SurfaceHighest else AppColors.SurfaceHigher
        val focusedBorder = if (isError) AppColors.Error else AppColors.Outline
        val unfocusedBorder = if (isError) AppColors.Error else Color.Transparent
        val errorBorder = AppColors.Error

        OutlinedTextField(
            modifier = modifier,
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            textStyle = AppTypography.Body1Regular,
            placeholder = {
                Text(
                    text = placeholder,
                    style = AppTypography.Body3Regular,
                    color = AppColors.TextSecondary,
                )
            },
            isError = isError,
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = focusedBorder,
                unfocusedBorderColor = unfocusedBorder,
                disabledBorderColor = Color.Transparent,
                errorBorderColor = errorBorder,

                focusedContainerColor = containerColor,
                unfocusedContainerColor = containerColor,
                disabledContainerColor = AppColors.SurfaceHighest,
                errorContainerColor = containerColor,

                cursorColor = AppColors.TextPrimary,
                focusedTextColor = AppColors.TextPrimary,
                unfocusedTextColor = AppColors.TextPrimary,
                errorTextColor = AppColors.TextPrimary,
            ),
        )
    }

    @Composable
    fun Search(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier = Modifier,
        placeholder: String = "Search for delicious food…",
    ) {
        OutlinedTextField(
            modifier = modifier
                .background(
                    color = AppColors.TextSecondary,
                    shape = RoundedCornerShape(28.dp),
                ),
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    style = AppTypography.Body1Regular,
                    color = AppColors.TextSecondary,
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.search),
                    contentDescription = null,
                    tint = AppColors.Primary,
                )
            },
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.Outline,
                unfocusedBorderColor = AppColors.Outline,
                disabledBorderColor = Color.Transparent,
                errorBorderColor = Color.Transparent,
                focusedContainerColor = AppColors.SurfaceHigher,
                unfocusedContainerColor = AppColors.SurfaceHigher,
                disabledContainerColor = AppColors.SurfaceHigher,
            ),
            singleLine = true,
        )
    }

    @Composable
    fun PhoneNumber(
        phoneNumber: String,
        onPhoneNumberChange: (String) -> Unit,
        modifier: Modifier = Modifier,
        isError: Boolean = false,
        enabled: Boolean = true,
        onFullPhoneNumberChange: (String) -> Unit = {},
        onValidityChange: (Boolean) -> Unit = {},
        initialCountryIso: String? = null,
        onCountryIsoChanged: (String) -> Unit = {},
    ) {
        PhoneNumberField(
            phoneNumber = phoneNumber,
            onPhoneNumberChange = onPhoneNumberChange,
            modifier = modifier,
            isError = isError,
            enabled = enabled,
            onFullPhoneNumberChange = onFullPhoneNumberChange,
            onValidityChange = onValidityChange,
            initialCountryIso = initialCountryIso,
            onCountryIsoChanged = onCountryIsoChanged,
        )
    }

    @Composable
    fun OtpCode(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier = Modifier,
        length: Int = 6,
        isError: Boolean = false,
        enabled: Boolean = true,
    ) {
        OtpCodeInput(
            value = value,
            onValueChange = onValueChange,
            length = length,
            isError = isError,
            enabled = enabled,
            modifier = modifier,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PrimaryStatesPreview() {
    LazyPizzaThemePreview {
        var empty by remember { mutableStateOf("") }
        var focusedDemo by remember { mutableStateOf(" ") }
        var filled by remember { mutableStateOf("Some text") }
        var error by remember { mutableStateOf("Invalid") }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DsTextField.Primary(
                value = empty,
                onValueChange = { empty = it },
                placeholder = "Label",
            )

            DsTextField.Primary(
                value = focusedDemo.trim(),
                onValueChange = { focusedDemo = it },
                placeholder = "Label",
            )

            DsTextField.Primary(
                value = filled,
                onValueChange = { filled = it },
                placeholder = "Label",
            )

            DsTextField.Primary(
                value = error,
                onValueChange = { error = it },
                placeholder = "Label",
                isError = true,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchPreview() {
    var searchQuery by remember { mutableStateOf("") }
    LazyPizzaThemePreview {
        DsTextField.Search(value = searchQuery, onValueChange = { searchQuery = it })
    }
}

@Preview(showBackground = true)
@Composable
private fun PhoneNumberPreview() {
    LazyPizzaThemePreview {
        var phone by remember { mutableStateOf("6 20 83 81 63") }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DsTextField.PhoneNumber(
                phoneNumber = phone,
                onPhoneNumberChange = { phone = it },
                onFullPhoneNumberChange = { },
                initialCountryIso = "FR",
            )
            DsTextField.PhoneNumber(
                phoneNumber = phone,
                onPhoneNumberChange = { phone = it },
                onFullPhoneNumberChange = { },
                initialCountryIso = "FR",
                enabled = false,
            )
            DsTextField.PhoneNumber(
                phoneNumber = phone,
                onPhoneNumberChange = { phone = it },
                isError = true,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OtpPreview() {
    LazyPizzaThemePreview {
        var otp1 by remember { mutableStateOf("") }
        var otp2 by remember { mutableStateOf("12") }
        var otp3 by remember { mutableStateOf("123456") }
        var otpError by remember { mutableStateOf("12") }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            DsTextField.OtpCode(
                value = otp1,
                onValueChange = { otp1 = it },
                length = 6,
            )

            DsTextField.OtpCode(
                value = otp2,
                onValueChange = { otp2 = it },
                length = 6,
            )

            DsTextField.OtpCode(
                value = otp3,
                onValueChange = { otp3 = it },
                length = 6,
            )

            DsTextField.OtpCode(
                value = otpError,
                onValueChange = { otpError = it },
                length = 6,
                isError = true,
            )
        }
    }
}
