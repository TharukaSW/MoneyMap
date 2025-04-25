package com.example.imilipocket.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.imilipocket.data.PreferenceManager
import com.example.imilipocket.data.Transaction
import java.util.*

class SettingsViewModel(private val preferenceManager: PreferenceManager) : ViewModel() {
    private val _currency = MutableLiveData<String>()
    val currency: LiveData<String> = _currency

    fun initialize() {
        loadCurrency()
    }

    private fun loadCurrency() {
        _currency.value = preferenceManager.getSelectedCurrency()
    }

    fun updateCurrency(newCurrency: String) {
        preferenceManager.setSelectedCurrency(newCurrency)
        _currency.value = newCurrency
    }

    fun setSelectedCurrency(currency: String) {
        val currencyCode = currency.substring(0, 3)
        preferenceManager.setSelectedCurrency(currencyCode)
    }

    fun getMonthlyTransactions(): List<Transaction> {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        // Get all transactions
        val allTransactions = preferenceManager.getTransactions()

        // Filter transactions for current month
        return allTransactions.filter { transaction ->
            calendar.timeInMillis = transaction.date
            calendar.get(Calendar.MONTH) == currentMonth && 
            calendar.get(Calendar.YEAR) == currentYear
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val preferenceManager = PreferenceManager(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!)
                SettingsViewModel(preferenceManager)
            }
        }
    }
} 