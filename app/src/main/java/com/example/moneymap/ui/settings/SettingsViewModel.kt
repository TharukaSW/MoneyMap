package com.example.moneymap.ui.settings

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.imilipocket.data.PreferenceManager
import com.example.imilipocket.data.Transaction
import java.util.*

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val preferenceManager: PreferenceManager = PreferenceManager(application)
    private val sharedPreferences: SharedPreferences = application.getSharedPreferences(
        "app_preferences",
        Application.MODE_PRIVATE
    )

    private val _currency = MutableLiveData<String>()
    val currency: LiveData<String> = _currency

    init {
        loadCurrency()
    }

    private fun loadCurrency() {
        _currency.value = preferenceManager.getSelectedCurrency()
    }

    fun updateCurrency(newCurrency: String) {
        preferenceManager.setSelectedCurrency(newCurrency)
        _currency.value = newCurrency
    }

    fun getMonthlyTransactions(): List<Transaction> {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        return preferenceManager.getTransactions().filter { transaction ->
            calendar.timeInMillis = transaction.date
            calendar.get(Calendar.MONTH) == currentMonth && 
            calendar.get(Calendar.YEAR) == currentYear
        }
    }

    fun isDarkModeEnabled(): Boolean {
        return sharedPreferences.getBoolean("dark_mode", false)
    }

    fun setDarkMode(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("dark_mode", enabled).apply()
    }
} 