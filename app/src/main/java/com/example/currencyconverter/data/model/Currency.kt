package com.example.currencyconverter.data.model

data class Currency(
    val code: String,
    val name: String
) {
    override fun toString(): String {
        return code
    }
}
