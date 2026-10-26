package com.example.currencyconverter.data.repository

import com.example.currencyconverter.data.api.CurrencyApi
import com.example.currencyconverter.data.model.Currency
import com.example.currencyconverter.data.model.Rate

class CurrencyRepository(private val api: CurrencyApi) {
    private val popularCodes = listOf(
        "usd", "eur", "rub", "gbp", "cny", "jpy", "kzt", "byn", "amd",
        "try", "aed", "chf", "gel", "uah", "inr", "krw", "cad", "aud", "pln"
    )

    private var currencyNames = mapOf<String, String>()

    suspend fun getCurrencies(): List<Currency> {
        val response = api.getCurrencies()
        currencyNames = response

        val result = mutableListOf<Currency>()
        for (code in popularCodes) {
            val name = response[code]
            if (name != null) {
                result.add(Currency(code.uppercase(), name))
            }
        }
        return result
    }

    suspend fun getRates(baseCode: String): List<Rate> {
        val base = baseCode.lowercase()
        val response = api.getRates(base)
        val ratesJson = response.getAsJsonObject(base)

        val result = mutableListOf<Rate>()
        for (code in popularCodes) {
            if (code == base) continue
            if (ratesJson.has(code)) {
                val value = ratesJson.get(code).asDouble
                val name = currencyNames[code] ?: code.uppercase()
                result.add(Rate(code.uppercase(), name, value))
            }
        }
        return result
    }
}
