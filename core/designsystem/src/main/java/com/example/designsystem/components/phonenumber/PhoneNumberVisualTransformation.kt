package com.example.designsystem.components.phonenumber

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PhoneNumberVisualTransformation(
    private val mask: String,
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        if (mask.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val digits = text.text.filter { it.isDigit() }

        val out = StringBuilder()
        var digitIndex = 0
        var maskIndex = 0

        while (maskIndex < mask.length && digitIndex < digits.length) {
            val maskChar = mask[maskIndex]
            if (maskChar == 'X') {
                out.append(digits[digitIndex])
                digitIndex++
            } else {
                out.append(maskChar)
            }
            maskIndex++
        }

        val formatted = out.toString()
        val maxDigits = mask.count { it == 'X' }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0

                val targetDigits = offset.coerceAtMost(maxDigits)

                var xSeen = 0
                var currentIndex = 0

                while (currentIndex < mask.length && xSeen < targetDigits) {
                    if (mask[currentIndex] == 'X') xSeen++
                    currentIndex++
                }
                return currentIndex.coerceAtMost(formatted.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return 0

                val safeOffset = offset.coerceAtMost(formatted.length)

                var xSeen = 0
                var currentIndex = 0

                while (currentIndex < safeOffset && currentIndex < mask.length) {
                    if (mask[currentIndex] == 'X') xSeen++
                    currentIndex++
                }
                return xSeen.coerceAtMost(maxDigits)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}
