package com.example.monestudey.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.monestudey.data.AppDatabase
import com.example.monestudey.data.Transaction
import com.example.monestudey.data.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Date

enum class TransactionFilter {
    ALL, INCOME, EXPENSE
}

class ExpenseTrackerViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val transactionDao = database.transactionDao()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _filter = MutableStateFlow(TransactionFilter.ALL)
    val filter: StateFlow<TransactionFilter> = _filter.asStateFlow()

    private val _filteredTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val filteredTransactions: StateFlow<List<Transaction>> = _filteredTransactions.asStateFlow()

    private val _totalIncome = MutableStateFlow(0.0)
    val totalIncome: StateFlow<Double> = _totalIncome.asStateFlow()

    private val _totalExpense = MutableStateFlow(0.0)
    val totalExpense: StateFlow<Double> = _totalExpense.asStateFlow()

    init {
        viewModelScope.launch {
            transactionDao.getAllTransactions().collect { transactions ->
                _transactions.value = transactions
                updateFilteredTransactions()
            }
        }

        viewModelScope.launch {
            transactionDao.getTotalByType(TransactionType.INCOME).collect { total ->
                _totalIncome.value = total ?: 0.0
            }
        }

        viewModelScope.launch {
            transactionDao.getTotalByType(TransactionType.EXPENSE).collect { total ->
                _totalExpense.value = total ?: 0.0
            }
        }
    }

    fun setFilter(filter: TransactionFilter) {
        _filter.value = filter
        updateFilteredTransactions()
    }

    private fun updateFilteredTransactions() {
        _filteredTransactions.value = when (_filter.value) {
            TransactionFilter.ALL -> _transactions.value
            TransactionFilter.INCOME -> _transactions.value.filter { it.type == TransactionType.INCOME }
            TransactionFilter.EXPENSE -> _transactions.value.filter { it.type == TransactionType.EXPENSE }
        }
    }

    fun addTransaction(amount: Double, description: String, category: String, type: TransactionType) {
        viewModelScope.launch {
            val transaction = Transaction(
                amount = amount,
                description = description,
                category = category,
                type = type,
                date = Date()
            )
            transactionDao.insertTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionDao.deleteTransaction(transaction)
        }
    }
} 