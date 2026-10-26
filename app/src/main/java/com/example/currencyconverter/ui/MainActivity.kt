package com.example.currencyconverter.ui

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.currencyconverter.data.model.Currency
import com.example.currencyconverter.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private var currencyList = listOf<Currency>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.etAmount.doAfterTextChanged {
            calculate()
        }

        binding.btnSwap.setOnClickListener {
            val fromPos = binding.spinnerFrom.selectedItemPosition
            val toPos = binding.spinnerTo.selectedItemPosition
            binding.spinnerFrom.setSelection(toPos)
            binding.spinnerTo.setSelection(fromPos)
        }

        binding.btnRetry.setOnClickListener {
            if (currencyList.isEmpty()) {
                viewModel.loadCurrencies()
            } else {
                viewModel.loadRates(getFromCode())
            }
        }

        binding.spinnerFrom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.loadRates(currencyList[position].code)
                calculate()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spinnerTo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                calculate()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun observeViewModel() {
        viewModel.currencies.observe(this) { list ->
            currencyList = list
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerFrom.adapter = adapter
            binding.spinnerTo.adapter = adapter

            val usdIndex = list.indexOfFirst { it.code == "USD" }
            val rubIndex = list.indexOfFirst { it.code == "RUB" }
            if (usdIndex != -1) binding.spinnerFrom.setSelection(usdIndex)
            if (rubIndex != -1) binding.spinnerTo.setSelection(rubIndex)
        }

        viewModel.rates.observe(this) {
            calculate()
        }

        viewModel.result.observe(this) { text ->
            binding.tvResult.text = text
        }

        viewModel.rateInfo.observe(this) { text ->
            binding.tvRateInfo.text = text
        }

        viewModel.isLoading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { message ->
            if (message != null) {
                binding.errorCard.visibility = View.VISIBLE
                binding.tvError.text = message
            } else {
                binding.errorCard.visibility = View.GONE
            }
        }
    }

    private fun getFromCode(): String {
        val pos = binding.spinnerFrom.selectedItemPosition
        if (pos < 0 || pos >= currencyList.size) return "USD"
        return currencyList[pos].code
    }

    private fun getToCode(): String {
        val pos = binding.spinnerTo.selectedItemPosition
        if (pos < 0 || pos >= currencyList.size) return "RUB"
        return currencyList[pos].code
    }

    private fun calculate() {
        if (currencyList.isEmpty()) return
        viewModel.convert(binding.etAmount.text.toString(), getFromCode(), getToCode())
    }
}
