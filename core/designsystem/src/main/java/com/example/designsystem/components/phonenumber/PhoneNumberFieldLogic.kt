package com.example.designsystem.components.phonenumber

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.VisualTransformation

internal fun filterCountries(
    all: List<PhoneCountryUi>,
    queryRaw: String,
): List<PhoneCountryUi> {
    val searchQuery = queryRaw.trim()
    if (searchQuery.isBlank()) return all
    return all.filter { country ->
        country.name.contains(searchQuery, ignoreCase = true) ||
            country.dialCode.contains(searchQuery) ||
            country.isoCode.contains(searchQuery, ignoreCase = true)
    }
}

internal fun extractDigits(
    raw: String,
    maxDigits: Int,
): String = raw.filter { it.isDigit() }.take(maxDigits)

internal fun buildFullNumber(
    local: String,
    country: PhoneCountryUi,
): String {
    val digitsOnly = local.filter { it.isDigit() }
    return country.dialCode + digitsOnly
}

internal data class MaskInfo(
    val visualTransformation: VisualTransformation,
    val maxDigitsForMask: Int,
    val placeholder: String,
)

@Composable
internal fun rememberMaskInfo(country: PhoneCountryUi): MaskInfo = remember(country) {
    val mask = country.phoneMask
    val maxDigits = mask.count { it == 'X' }.coerceAtLeast(0)
    val placeholder = buildPlaceholderFromMask(mask)

    MaskInfo(
        visualTransformation = PhoneNumberVisualTransformation(mask),
        maxDigitsForMask = maxDigits,
        placeholder = placeholder,
    )
}

internal fun buildPlaceholderFromMask(mask: String): String = mask.map { ch ->
    if (ch == 'X') '0' else ch
}.joinToString("")
