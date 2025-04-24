package com.example.imilipocket.data

import android.content.Context
import android.content.SharedPreferences
import com.example.imilipocket.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    private val context: Context = context

    companion object {
        private const val PREFS_NAME = "ImiliPocketPrefs"
        private const val KEY_TRANSACTIONS = "transactions"
        private const val KEY_MONTHLY_BUDGET = "monthly_budget"
        private const val KEY_SELECTED_CURRENCY = "selected_currency"
        private const val KEY_MONTH_CYCLE_START_DAY = "month_cycle_start_day"
        private const val DEFAULT_CURRENCY = "USD"
        private const val DEFAULT_MONTH_CYCLE_START_DAY = 1
    }

    fun saveMonthlyBudget(budget: Double) {
        try {
            sharedPreferences.edit().putFloat(KEY_MONTHLY_BUDGET, budget.toFloat()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getMonthlyBudget(): Double {
        return try {
            sharedPreferences.getFloat(KEY_MONTHLY_BUDGET, 0f).toDouble()
        } catch (e: Exception) {
            e.printStackTrace()
            0.0
        }
    }

    fun getMonthCycleStartDay(): Int {
        return try {
            sharedPreferences.getInt(KEY_MONTH_CYCLE_START_DAY, DEFAULT_MONTH_CYCLE_START_DAY)
        } catch (e: Exception) {
            e.printStackTrace()
            DEFAULT_MONTH_CYCLE_START_DAY
        }
    }

    fun setMonthCycleStartDay(day: Int) {
        try {
            if (day in 1..31) {
                sharedPreferences.edit().putInt(KEY_MONTH_CYCLE_START_DAY, day).apply()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getMonthlyExpenses(): Double {
        return try {
            val calendar = Calendar.getInstance()
            val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
            val monthCycleStartDay = getMonthCycleStartDay()
            
            // Adjust the month if current day is before the cycle start day
            if (currentDay < monthCycleStartDay) {
                calendar.add(Calendar.MONTH, -1)
            }
            
            val cycleMonth = calendar.get(Calendar.MONTH)
            val cycleYear = calendar.get(Calendar.YEAR)
            
            getTransactions()
                .filter { 
                    val transactionDate = Calendar.getInstance().apply { 
                        timeInMillis = it.date 
                    }
                    val transactionDay = transactionDate.get(Calendar.DAY_OF_MONTH)
                    val transactionMonth = transactionDate.get(Calendar.MONTH)
                    val transactionYear = transactionDate.get(Calendar.YEAR)
                    
                    // Check if transaction falls within the current cycle
                    if (transactionDay >= monthCycleStartDay) {
                        transactionMonth == cycleMonth && transactionYear == cycleYear
                    } else {
                        // If transaction is before cycle start day, it belongs to previous month
                        val prevMonth = if (cycleMonth == 0) 11 else cycleMonth - 1
                        val prevYear = if (cycleMonth == 0) cycleYear - 1 else cycleYear
                        transactionMonth == prevMonth && transactionYear == prevYear
                    } && it.type == Transaction.Type.EXPENSE
                }
                .sumOf { it.amount }
        } catch (e: Exception) {
            e.printStackTrace()
            0.0
        }
    }

    fun saveTransactions(transactions: List<Transaction>) {
        try {
            val json = gson.toJson(transactions)
            sharedPreferences.edit().putString(KEY_TRANSACTIONS, json).apply()
        } catch (e: Exception) {
            e.printStackTrace()
            // If saving fails, try to save an empty list as fallback
            try {
                sharedPreferences.edit().putString(KEY_TRANSACTIONS, "[]").apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getTransactions(): List<Transaction> {
        return try {
            val json = sharedPreferences.getString(KEY_TRANSACTIONS, "[]")
            val type = object : TypeToken<List<Transaction>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun addTransaction(transaction: Transaction) {
        try {
            val transactions = getTransactions().toMutableList()
            transactions.add(transaction)
            saveTransactions(transactions)
        } catch (e: Exception) {
            e.printStackTrace()
            // If adding fails, try to initialize with just this transaction
            try {
                saveTransactions(listOf(transaction))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateTransaction(transaction: Transaction) {
        try {
            val transactions = getTransactions().toMutableList()
            val index = transactions.indexOfFirst { it.id == transaction.id }
            if (index != -1) {
                transactions[index] = transaction
                saveTransactions(transactions)
            } else {
                throw Exception("Transaction not found")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // If update fails, try to preserve existing data
            try {
                val currentTransactions = getTransactions()
                saveTransactions(currentTransactions)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        val transactions = getTransactions().toMutableList()
        transactions.removeIf { it.id == transaction.id }
        saveTransactions(transactions)
    }

    fun getSelectedCurrency(): String {
        return try {
            sharedPreferences.getString(KEY_SELECTED_CURRENCY, DEFAULT_CURRENCY) ?: DEFAULT_CURRENCY
        } catch (e: Exception) {
            e.printStackTrace()
            DEFAULT_CURRENCY
        }
    }

    fun setSelectedCurrency(currency: String) {
        try {
            sharedPreferences.edit().putString(KEY_SELECTED_CURRENCY, currency).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCategories(): List<String> {
        return try {
            context.resources.getStringArray(R.array.transaction_categories).toList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
} 