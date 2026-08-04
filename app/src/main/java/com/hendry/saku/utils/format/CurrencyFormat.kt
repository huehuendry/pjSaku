package com.hendry.saku.utils.format

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.NumberFormat
import java.util.Locale

fun Long.toRupiah(): String {

    val localeID = Locale("in", "ID")

    val formatter =
        NumberFormat.getCurrencyInstance(localeID)

    formatter.maximumFractionDigits = 0

    return formatter.format(this)
}

class CurrencyVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text

        val formatted = if (digits.isBlank()) {
            ""
        } else {
            digits.toLongOrNull()?.let { value ->
                val nf = NumberFormat.getNumberInstance(Locale("in", "ID"))
                nf.maximumFractionDigits = 0
                nf.format(value)
            } ?: digits
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (digits.isBlank()) return 0
                val dotsBeforeOffset = formatted.take(
                    mapOriginalToFormatted(digits, formatted, offset)
                ).count { it == '.' }
                return (offset + dotsBeforeOffset).coerceAtMost(formatted.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (formatted.isBlank()) return 0
                val digitsOnly = formatted.replace(".", "")
                var digitsSeen = 0
                for (i in 0 until offset.coerceAtMost(formatted.length)) {
                    if (formatted[i] != '.') digitsSeen++
                }
                return digitsSeen.coerceAtMost(digits.length)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }

    private fun mapOriginalToFormatted(original: String, formatted: String, originalOffset: Int): Int {
        var digitCount = 0
        for (i in formatted.indices) {
            if (formatted[i] != '.') {
                digitCount++
                if (digitCount == originalOffset) return i + 1
            }
        }
        return formatted.length
    }
}