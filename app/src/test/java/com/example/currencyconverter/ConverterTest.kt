package com.example.currencyconverter

import com.example.currencyconverter.utils.Converter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ConverterTest {
    @Test
    fun convert_isCorrect() {
        val result = Converter.convert(100.0, 90.5)
        assertEquals(9050.0, result, 0.0001)
    }

    @Test
    fun convert_zeroAmount() {
        assertEquals(0.0, Converter.convert(0.0, 90.5), 0.0001)
    }

    @Test
    fun convert_negativeAmount_returnsZero() {
        assertEquals(0.0, Converter.convert(-5.0, 90.5), 0.0001)
    }

    @Test
    fun parseAmount_withComma() {
        assertEquals(12.5, Converter.parseAmount("12,5")!!, 0.0001)
    }

    @Test
    fun parseAmount_withDot() {
        assertEquals(12.5, Converter.parseAmount(" 12.5 ")!!, 0.0001)
    }

    @Test
    fun parseAmount_emptyOrWrong_returnsNull() {
        assertNull(Converter.parseAmount(""))
        assertNull(Converter.parseAmount("abc"))
    }
}
