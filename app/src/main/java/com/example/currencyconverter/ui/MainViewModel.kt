package com.example.currencyconverter.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.data.api.RetrofitClient
import com.example.currencyconverter.data.model.Currency
import com.example.currencyconverter.data.model.Rate
import com.example.currencyconverter.data.repository.CurrencyRepository
import com.example.currencyconverter.utils.Converter
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository = CurrencyRepository(RetrofitClient.api)

    private val _currencies = MutableLiveData<List<Currency>>()
    val currencies: LiveData<List<Currency>> = _currencies

    private val _rates = MutableLiveData<List<Rate>>()
    val rates: LiveData<List<Rate>> = _rates

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result

    private val _rateInfo = MutableLiveData<String>()
    val rateInfo: LiveData<String> = _rateInfo

    private var loadedBase = ""

    init {
        loadCurrencies()
    }

    fun loadCurrencies() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _currencies.value = repository.getCurrencies()
            } catch (e: Exception) {
                _error.value = "Не удалось загрузить валюты. Проверьте интернет"
            }
            _isLoading.value = false
        }
    }

    fun loadRates(base: String) {
        if (base == loadedBase) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val list = repository.getRates(base)
                loadedBase = base
                _rates.value = list
            } catch (e: Exception) {
                _error.value = "Не удалось загрузить курсы. Проверьте интернет"
            }
            _isLoading.value = false
        }
    }

    fun convert(amountText: String, from: String, to: String) {
        if (from == to) {
            _rateInfo.value = "1 $from = 1 $to"
            val amount = Converter.parseAmount(amountText)
            _result.value = if (amount == null) "—" else Converter.format(amount) + " " + to
            return
        }

        if (loadedBase != from) {
            _rateInfo.value = "Загрузка курса…"
            _result.value = "…"
            return
        }

        val rate = _rates.value?.find { it.code == to }
        if (rate == null) {
            _rateInfo.value = ""
            _result.value = "—"
            return
        }

        _rateInfo.value = "1 $from = " + Converter.format(rate.value) + " " + to

        val amount = Converter.parseAmount(amountText)
        if (amount == null) {
            _result.value = "—"
            return
        }

        val value = Converter.convert(amount, rate.value)
        _result.value = Converter.format(value) + " " + to
    }
}
