package com.example.monestudey.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.monestudey.data.AppDatabase
import com.example.monestudey.data.Transaction
import com.example.monestudey.data.TransactionType
import com.example.monestudey.data.entities.Category
import com.example.monestudey.data.entities.CategoryType
import com.example.monestudey.data.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date

enum class TransactionFilter {
    ALL, INCOME, EXPENSE
}

class ExpenseTrackerViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val transactionRepository = TransactionRepository(database.transactionDao())
    private val categoryDao = database.categoryDao()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _filteredTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val filteredTransactions: StateFlow<List<Transaction>> = _filteredTransactions.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _incomeCategories = MutableStateFlow<List<Category>>(emptyList())
    val incomeCategories: StateFlow<List<Category>> = _incomeCategories.asStateFlow()

    private val _expenseCategories = MutableStateFlow<List<Category>>(emptyList())
    val expenseCategories: StateFlow<List<Category>> = _expenseCategories.asStateFlow()

    private val _filter = MutableStateFlow(TransactionFilter.ALL)
    val filter: StateFlow<TransactionFilter> = _filter.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    private val _totalIncome = MutableStateFlow(0.0)
    val totalIncome: StateFlow<Double> = _totalIncome.asStateFlow()

    private val _totalExpense = MutableStateFlow(0.0)
    val totalExpense: StateFlow<Double> = _totalExpense.asStateFlow()

    init {
        viewModelScope.launch {
            // Cargar transacciones
            transactionRepository.getAllTransactions().collect { transactions ->
                _transactions.value = transactions
                updateFilteredTransactions()
                updateTotals(transactions)
            }
        }

        viewModelScope.launch {
            // Cargar todas las categorías
            categoryDao.getAllCategories().collect { categories ->
                _categories.value = categories
                // Separar categorías por tipo
                _incomeCategories.value = categories.filter { it.type == CategoryType.INCOME }
                _expenseCategories.value = categories.filter { it.type == CategoryType.EXPENSE }
            }
        }
    }

    fun setFilter(filter: TransactionFilter) {
        _filter.value = filter
        updateFilteredTransactions()
    }

    fun setSelectedCategory(category: Category?) {
        _selectedCategory.value = category
        updateFilteredTransactions()
    }

    private fun updateFilteredTransactions() {
        val filtered = when (_filter.value) {
            TransactionFilter.INCOME -> _transactions.value.filter { it.type == TransactionType.INCOME }
            TransactionFilter.EXPENSE -> _transactions.value.filter { it.type == TransactionType.EXPENSE }
            TransactionFilter.ALL -> _transactions.value
        }
        _filteredTransactions.value = filtered
    }

    private fun updateTotals(transactions: List<Transaction>) {
        _totalIncome.value = transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }

        _totalExpense.value = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
    }

    fun addTransaction(
        amount: Double,
        description: String,
        categoryId: Long,
        type: TransactionType,
        isRecurring: Boolean = false,
        recurrencePattern: String? = null
    ) {
        viewModelScope.launch {
            val transaction = Transaction(
                amount = amount,
                description = description,
                categoryId = categoryId,
                type = type,
                date = Date(),
                isRecurring = isRecurring,
                recurrencePattern = recurrencePattern
            )
            transactionRepository.insertTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction)
        }
    }

    fun getCategoriesByType(type: CategoryType): Flow<List<Category>> {
        return categoryDao.getCategoriesByType(type)
    }
}