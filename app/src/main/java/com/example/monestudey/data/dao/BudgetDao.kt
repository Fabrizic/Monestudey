package com.example.monestudey.data.dao

import androidx.room.*
import com.example.monestudey.data.entities.Budget
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE isActive = 1 ORDER BY startDate DESC")
    fun getActiveBudgets(): Flow<List<Budget>>

    @Query("SELECT * FROM budgets WHERE categoryId = :categoryId AND isActive = 1")
    fun getBudgetByCategory(categoryId: Long): Flow<Budget?>

    @Query("SELECT * FROM budgets WHERE startDate <= :date AND endDate >= :date AND isActive = 1")
    fun getCurrentBudgets(date: Date = Date()): Flow<List<Budget>>

    @Insert
    suspend fun insertBudget(budget: Budget): Long

    @Update
    suspend fun updateBudget(budget: Budget)

    @Delete
    suspend fun deleteBudget(budget: Budget)

    @Query("SELECT SUM(amount) FROM budgets WHERE isActive = 1")
    fun getTotalBudget(): Flow<Double?>
} 