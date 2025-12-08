package com.example.designsystem.components.textfield

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.AppColors
import com.example.designsystem.theme.AppTypography

@Composable
internal fun PhoneNumberField(
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    enabled: Boolean = true,
    onFullPhoneNumberChange: (String) -> Unit = {},
    onValidityChange: (Boolean) -> Unit = {},
) {
    val (allCountries, defaultCountry) = PhoneCountryProvider.rememberPhoneCountriesWithDefault()

    var selectedCountry by remember { mutableStateOf(defaultCountry) }
    var isCountryPickerOpen by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredCountries = remember(allCountries, searchQuery) {
        filterCountries(allCountries, searchQuery)
    }

    val maskInfo = rememberMaskInfo(selectedCountry)

    val containerColor = if (phoneNumber.isEmpty()) AppColors.SurfaceHighest else AppColors.SurfaceHigher
    val focusedBorder = if (isError) AppColors.Error else AppColors.Outline
    val unfocusedBorder = if (isError) AppColors.Error else Color.Transparent
    val errorBorder = AppColors.Error

    fun updateFullNumber(
        local: String = phoneNumber,
        country: PhoneCountryUi = selectedCountry,
    ) {
        val full = buildFullNumber(local, country)
        onFullPhoneNumberChange(full)
    }

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = phoneNumber,
            onValueChange = { raw ->
                val sanitized = extractDigits(raw, maskInfo.maxDigitsForMask)
                onPhoneNumberChange(sanitized)
                updateFullNumber(local = sanitized)
                val isComplete = sanitized.length == maskInfo.maxDigitsForMask
                onValidityChange(isComplete)
            },
            enabled = enabled,
            singleLine = true,
            textStyle = AppTypography.Body1Regular,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = isError,
            placeholder = {
                Text(
                    text = maskInfo.placeholder,
                    style = AppTypography.Body1Regular,
                    color = AppColors.TextSecondary,
                )
            },
            visualTransformation = maskInfo.visualTransformation,
            leadingIcon = {
                CountryLeadingIcon(
                    country = selectedCountry,
                    enabled = enabled,
                    onClick = { isCountryPickerOpen = true },
                )
            },
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

        CountryDropdown(
            expanded = isCountryPickerOpen,
            searchQuery = searchQuery,
            countries = filteredCountries,
            onDismissRequest = { isCountryPickerOpen = false },
            onSearchQueryChange = { searchQuery = it },
            onCountrySelected = { country ->
                val newMax = country.phoneMask.count { it == 'X' }.coerceAtLeast(0)
                val newLocal = phoneNumber.filter { it.isDigit() }.take(newMax)

                selectedCountry = country
                isCountryPickerOpen = false
                searchQuery = ""

                if (newLocal != phoneNumber) {
                    onPhoneNumberChange(newLocal)
                }
                updateFullNumber(local = newLocal, country = country)
                val isComplete = newLocal.length == newMax
                onValidityChange(isComplete)
            },
        )
    }
}
