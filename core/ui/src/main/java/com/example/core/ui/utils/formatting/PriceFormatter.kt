package com.example.core.ui.utils.formatting

import java.text.NumberFormat
import java.util.Locale

fun Double.toFormattedCurrency(locale: Locale = Locale.getDefault()): String {
    val format = NumberFormat.getCurrencyInstance(locale)
    format.minimumFractionDigits = 2
    format.maximumFractionDigits = 2
    return format.format(this)
}
