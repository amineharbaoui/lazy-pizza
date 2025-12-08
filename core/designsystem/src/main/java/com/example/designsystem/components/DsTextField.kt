package com.example.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.designsystem.R
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
        val containerColor = if (value.isEmpty()) AppColors.SurfaceHighest else AppColors.SurfaceHigher
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
//                focusedContainerColor = containerColor,
//                unfocusedContainerColor = containerColor,
//                disabledContainerColor = AppColors.SurfaceHighest,
//                errorContainerColor = containerColor,

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

    data class PhoneCountryUi(
        val isoCode: String,
        val name: String,
        val dialCode: String,
        val flagResId: Int,
    )

    @Composable
    fun PhoneNumber(
        phoneNumber: String,
        onPhoneNumberChange: (String) -> Unit,
        modifier: Modifier = Modifier,
        placeholder: String = "(201) 555-0123",
        isError: Boolean = false,
        enabled: Boolean = true,
        onFullPhoneNumberChange: (String) -> Unit = {},
    ) {
        val allCountries = PhoneCountryProvider.rememberPhoneCountries()
        var selectedCountry by remember { mutableStateOf(allCountries.last()) }
        var isMenuOpen by remember { mutableStateOf(false) }
        var searchQuery by remember { mutableStateOf("") }

        val filteredCountries = remember(searchQuery) {
            val q = searchQuery.trim()
            if (q.isBlank()) {
                allCountries
            } else {
                allCountries.filter { c ->
                    c.name.contains(q, ignoreCase = true) ||
                        c.dialCode.contains(q) ||
                        c.isoCode.contains(q, ignoreCase = true)
                }
            }
        }

        fun updateFullNumber(
            local: String = phoneNumber,
            country: PhoneCountryUi = selectedCountry,
        ) {
            val digitsOnly = local.filter { it.isDigit() }
            val full = country.dialCode + digitsOnly
            onFullPhoneNumberChange(full)
        }

        val containerColor =
            if (phoneNumber.isEmpty()) AppColors.SurfaceHighest else AppColors.SurfaceHigher
        val focusedBorder = if (isError) AppColors.Error else AppColors.Outline
        val unfocusedBorder = if (isError) AppColors.Error else Color.Transparent
        val errorBorder = AppColors.Error

        Box(modifier = modifier) {
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                    onPhoneNumberChange(it)
                    updateFullNumber(local = it)
                },
                enabled = enabled,
                singleLine = true,
                textStyle = AppTypography.Body1Regular,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = isError,
                placeholder = {
                    Text(
                        text = placeholder,
                        style = AppTypography.Body1Regular,
                        color = AppColors.TextSecondary,
                    )
                },
                leadingIcon = {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(28.dp))
                            .clickable(enabled = enabled) { isMenuOpen = true },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Spacer(Modifier.width(8.dp))
                        Image(
                            painter = painterResource(selectedCountry.flagResId),
                            contentDescription = selectedCountry.name,
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
                            text = selectedCountry.dialCode,
                            style = AppTypography.Body1Regular,
                            color = AppColors.TextPrimary,
                        )
                    }
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

            DropdownMenu(
                modifier = Modifier.background(AppColors.Bg),
                shape = RoundedCornerShape(12.dp),
                expanded = isMenuOpen,
                onDismissRequest = { isMenuOpen = false },
            ) {
                Column(
                    modifier = Modifier
                        .width(280.dp)
                        .padding(horizontal = 8.dp),
                ) {
                    // Search field
                    Search(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = "Search for countries",
                    )
                    Spacer(Modifier.height(8.dp))

                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                            .verticalScroll(scrollState),
                    ) {
                        filteredCountries.forEach { country ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        selectedCountry = country
                                        isMenuOpen = false
                                        searchQuery = ""
                                        updateFullNumber(country = country)
                                    }
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
                    }
                }
            }
        }
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
