package com.example.core.designsystem.components.phonenumber

import androidx.annotation.DrawableRes

data class PhoneCountryDisplayModel(
    val isoCode: String,
    val name: String,
    val dialCode: String,
    @param:DrawableRes
    val flagResId: Int,
    val phoneMask: String,
)
