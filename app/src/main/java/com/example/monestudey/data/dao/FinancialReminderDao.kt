package com.example.monestudey.data.dao

import androidx.room.*
import com.example.monestudey.data.entities.FinancialReminder
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface FinancialReminderDao {
    @Query("SELECT * FROM financial_reminders ORDER BY dueDate ASC")
    fun getAllReminders(): Flow<List<FinancialReminder>>

    @Query("SELECT * FROM financial_reminders WHERE isCompleted = 0 ORDER BY dueDate ASC")
    fun getActiveReminders(): Flow<List<FinancialReminder>>

    @Query("SELECT * FROM financial_reminders WHERE dueDate <= :date AND isCompleted = 0 ORDER BY dueDate ASC")
    fun getUpcomingReminders(date: Date = Date()): Flow<List<FinancialReminder>>

    @Query("SELECT * FROM financial_reminders WHERE categoryId = :categoryId AND isCompleted = 0 ORDER BY dueDate ASC")
    fun getRemindersByCategory(categoryId: Long): Flow<List<FinancialReminder>>

    @Insert
    suspend fun insertReminder(reminder: FinancialReminder): Long

    @Update
    suspend fun updateReminder(reminder: FinancialReminder)

    @Delete
    suspend fun deleteReminder(reminder: FinancialReminder)

    @Query("SELECT COUNT(*) FROM financial_reminders WHERE isCompleted = 0")
    fun getActiveRemindersCount(): Flow<Int>
} 