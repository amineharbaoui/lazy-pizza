package com.example.designsystem.components

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
import com.example.designsystem.R
import com.example.designsystem.components.textfield.PhoneNumberField
import com.example.designsystem.theme.AppColors
import com.example.designsystem.theme.AppTypography
import com.example.designsystem.theme.LazyPizzaThemePreview

object DsTextField {

    @Composable
    fun Primary(
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        modifier: Modifier = Modifier,
        isError: Boolean = false,
        enabled: Boolean = true,
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
            singleLine = true,
            textStyle = AppTypography.Body1Regular,
            placeholder = {
                Text(
                    text = label,
                    style = AppTypography.Body1Regular,
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
                .background(AppColors.SurfaceHigher, RoundedCornerShape(28.dp)),
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
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
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
    ) {
        PhoneNumberField(
            phoneNumber = phoneNumber,
            onPhoneNumberChange = onPhoneNumberChange,
            modifier = modifier,
            isError = isError,
            enabled = enabled,
            onFullPhoneNumberChange = onFullPhoneNumberChange,
            onValidityChange = onValidityChange,
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
                label = "Label",
            )

            DsTextField.Primary(
                value = focusedDemo.trim(),
                onValueChange = { focusedDemo = it },
                label = "Label",
            )

            DsTextField.Primary(
                value = filled,
                onValueChange = { filled = it },
                label = "Label",
            )

            DsTextField.Primary(
                value = error,
                onValueChange = { error = it },
                label = "Label",
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
        var phone by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DsTextField.PhoneNumber(
                phoneNumber = phone,
                onPhoneNumberChange = { phone = it },
                onFullPhoneNumberChange = { full ->

                    println("Full phone: $full")
                },
            )

            DsTextField.PhoneNumber(
                phoneNumber = phone,
                onPhoneNumberChange = { phone = it },
                isError = true,
            )
        }
    }
}
