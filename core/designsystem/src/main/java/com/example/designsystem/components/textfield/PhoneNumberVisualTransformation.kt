package com.example.designsystem.components.textfield

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PhoneNumberVisualTransformation(
    private val mask: String,
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }

        val out = StringBuilder()
        var digitIndex = 0
        var maskIndex = 0

        while (maskIndex < mask.length && digitIndex < digits.length) {
            val c = mask[maskIndex]
            if (c == 'X') {
                out.append(digits[digitIndex])
                digitIndex++
            } else {
                out.append(c)
            }
            maskIndex++
        }

        val formatted = out.toString()

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0

                var xSeen = 0
                var pos = 0

                while (pos < mask.length && pos < formatted.length && xSeen < offset) {
                    if (mask[pos] == 'X') {
                        xSeen++
                    }
                    pos++
                }
                return pos
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return 0

                var xSeen = 0
                var pos = 0

                while (pos < mask.length && pos < offset && pos < formatted.length) {
                    if (mask[pos] == 'X') {
                        xSeen++
                    }
                    pos++
                }
                return xSeen
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}
