package com.example.core.ui.utils.formatting

import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

interface CurrencyFormatter {
    fun format(value: Double): String
}

class DefaultCurrencyFormatter @Inject constructor() : CurrencyFormatter {

    private val locale: Locale = Locale.getDefault()

    override fun format(value: Double): String {
        val format = NumberFormat.getCurrencyInstance(locale)
        format.minimumFractionDigits = 2
        format.maximumFractionDigits = 2
        return format.format(value)
    }
}
