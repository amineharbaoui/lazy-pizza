package com.example.designsystem.components.phonenumber

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.core.designsystem.R
import com.example.designsystem.components.DsTextField
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
    initialCountryIso: String? = null,
    onCountryIsoChanged: (String) -> Unit = {},
) {
    val (allCountries, defaultCountry) = PhoneCountryProvider.rememberPhoneCountriesWithDefault()

    var selectedCountry by remember(allCountries, defaultCountry, initialCountryIso) {
        val initial = initialCountryIso?.let { iso ->
            allCountries.firstOrNull { it.isoCode.equals(iso, ignoreCase = true) }
        } ?: defaultCountry
        mutableStateOf(initial)
    }
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

    LaunchedEffect(selectedCountry.isoCode) {
        onCountryIsoChanged(selectedCountry.isoCode)
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
                disabledTextColor = AppColors.TextSecondary,
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
                onCountryIsoChanged(country.isoCode)
            },
        )
    }
}

@Composable
private fun CountryLeadingIcon(
    country: PhoneCountryUi,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(28.dp))
            .clickable(enabled = enabled) { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.width(8.dp))
        Image(
            painter = painterResource(country.flagResId),
            contentDescription = country.name,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
        Spacer(Modifier.width(4.dp))
        Icon(
            painter = painterResource(R.drawable.chevron_down),
            contentDescription = null,
            tint = AppColors.TextSecondary,
            modifier = Modifier.size(16.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = country.dialCode,
            style = AppTypography.Body1Regular,
            color = if (enabled) AppColors.TextPrimary else AppColors.TextSecondary,
        )
    }
}

@Composable
private fun CountryDropdown(
    expanded: Boolean,
    searchQuery: String,
    countries: List<PhoneCountryUi>,
    onDismissRequest: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCountrySelected: (PhoneCountryUi) -> Unit,
) {
    DropdownMenu(
        modifier = Modifier.background(AppColors.Bg),
        shape = RoundedCornerShape(12.dp),
        expanded = expanded,
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .width(280.dp)
                .padding(horizontal = 8.dp),
        ) {
            DsTextField.Search(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = "Search for countries",
            )
            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
            ) {
                val listState = rememberLazyListState()
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                ) {
                    items(
                        count = countries.size,
                        key = { index -> countries[index].isoCode },
                    ) { index ->
                        val country = countries[index]
                        CountryRow(
                            country = country,
                            onClick = { onCountrySelected(country) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CountryRow(
    country: PhoneCountryUi,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(country.flagResId),
            contentDescription = country.name,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = country.name,
            style = AppTypography.Body3Regular,
            color = AppColors.TextPrimary,
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = country.dialCode,
            style = AppTypography.Body3Regular,
            color = AppColors.TextSecondary,
        )
    }
}
