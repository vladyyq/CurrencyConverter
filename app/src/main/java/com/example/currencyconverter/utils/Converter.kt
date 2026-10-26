package com.example.currencyconverter.utils

import java.util.Locale

object Converter {
    fun convert(amount: Double, rate: Double): Double {
        if (amount < 0 || rate < 0) {
            return 0.0
        }
        return amount * rate
    }

    fun parseAmount(text: String): Double? {
        val fixed = text.trim().replace(",", ".")
        if (fixed.isEmpty()) return null
        return fixed.toDoubleOrNull()
    }

    fun format(value: Double): String {
        return if (value >= 1 || value == 0.0) {
            String.format(Locale("ru"), "%,.2f", value)
        } else {
            String.format(Locale("ru"), "%.6f", value)
        }
    }
}
