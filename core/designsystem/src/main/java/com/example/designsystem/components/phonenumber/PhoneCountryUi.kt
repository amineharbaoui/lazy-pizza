package com.example.designsystem.components.phonenumber

import androidx.annotation.DrawableRes

data class PhoneCountryUi(
    val isoCode: String,
    val name: String,
    val dialCode: String,
    @param:DrawableRes
    val flagResId: Int,
    val phoneMask: String,
)
