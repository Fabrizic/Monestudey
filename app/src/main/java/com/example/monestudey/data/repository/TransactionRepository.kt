package com.example.monestudey.data.repository

import com.example.monestudey.data.Transaction
import com.example.monestudey.data.TransactionType
import com.example.monestudey.data.dao.TransactionDao
import kotlinx.coroutines.flow.Flow
import java.util.Date

class TransactionRepository(private val transactionDao: TransactionDao) {
    
    fun getAllTransactions(): Flow<List<Transaction>> = 
        transactionDao.getAllTransactions()

    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> =
        transactionDao.getTransactionsByType(type)

    fun getTransactionsByCategory(categoryId: Long): Flow<List<Transaction>> =
        transactionDao.getTransactionsByCategory(categoryId)

    fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<Transaction>> =
        transactionDao.getTransactionsByDateRange(startDate, endDate)

    fun getTotalByType(type: TransactionType): Flow<Double?> =
        transactionDao.getTotalByType(type)

    fun getTotalByCategoryAndType(categoryId: Long, type: TransactionType): Flow<Double?> =
        transactionDao.getTotalByCategoryAndType(categoryId, type)

    suspend fun insertTransaction(transaction: Transaction): Long =
        transactionDao.insertTransaction(transaction)

    suspend fun updateTransaction(transaction: Transaction) =
        transactionDao.updateTransaction(transaction)

    suspend fun deleteTransaction(transaction: Transaction) =
        transactionDao.deleteTransaction(transaction)

    suspend fun getTransactionById(id: Long): Transaction? =
        transactionDao.getTransactionById(id)
} 