package com.example.imilipocket.util

import android.content.Context
import com.example.imilipocket.data.PreferenceManager
import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    fun formatAmount(context: Context, amount: Double): String {
        val preferenceManager = PreferenceManager(context)
        val currency = preferenceManager.getSelectedCurrency()
        val locale = getLocaleForCurrency(currency)
        val format = NumberFormat.getCurrencyInstance(locale)
        return format.format(amount)
    }

    private fun getLocaleForCurrency(currency: String): Locale {
        return when (currency) {
            "USD" -> Locale.US
            "EUR" -> Locale.GERMANY
            "GBP" -> Locale.UK
            "JPY" -> Locale.JAPAN
            "INR" -> Locale("en", "IN")
            "AUD" -> Locale("en", "AU")
            "CAD" -> Locale("en", "CA")
            "LKR" -> Locale("si", "LK")
            "CNY" -> Locale("zh", "CN")
            "SGD" -> Locale("en", "SG")
            "MYR" -> Locale("ms", "MY")
            "THB" -> Locale("th", "TH")
            "IDR" -> Locale("id", "ID")
            "PHP" -> Locale("en", "PH")
            "VND" -> Locale("vi", "VN")
            "KRW" -> Locale("ko", "KR")
            "AED" -> Locale("ar", "AE")
            "SAR" -> Locale("ar", "SA")
            "QAR" -> Locale("ar", "QA")
            else -> Locale.US
        }
    }
} 