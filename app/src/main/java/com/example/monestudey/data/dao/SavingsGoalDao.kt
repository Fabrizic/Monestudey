package com.example.monestudey.data.dao

import androidx.room.*
import com.example.monestudey.data.entities.SavingsGoal
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingsGoalDao {
    @Query("SELECT * FROM savings_goals ORDER BY targetDate ASC")
    fun getAllSavingsGoals(): Flow<List<SavingsGoal>>

    @Query("SELECT * FROM savings_goals WHERE isCompleted = 0 ORDER BY targetDate ASC")
    fun getActiveSavingsGoals(): Flow<List<SavingsGoal>>

    @Insert
    suspend fun insertSavingsGoal(savingsGoal: SavingsGoal): Long

    @Update
    suspend fun updateSavingsGoal(savingsGoal: SavingsGoal)

    @Delete
    suspend fun deleteSavingsGoal(savingsGoal: SavingsGoal)

    @Query("SELECT SUM(currentAmount) FROM savings_goals WHERE isCompleted = 0")
    fun getTotalCurrentSavings(): Flow<Double?>

    @Query("SELECT SUM(targetAmount) FROM savings_goals WHERE isCompleted = 0")
    fun getTotalTargetSavings(): Flow<Double?>
} 