package com.example.currencyconverter.data.api

import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyApi {
    @GET("currencies.json")
    suspend fun getCurrencies(): Map<String, String>

    @GET("currencies/{base}.json")
    suspend fun getRates(@Path("base") base: String): JsonObject
}
