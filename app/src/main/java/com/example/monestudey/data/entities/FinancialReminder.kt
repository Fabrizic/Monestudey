package com.example.monestudey.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index
import java.util.Date

@Entity(
    tableName = "financial_reminders",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["categoryId"])]
)
data class FinancialReminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val amount: Double,
    val dueDate: Date,
    val categoryId: Long,
    val isRecurring: Boolean = false,
    val recurrencePattern: String? = null,
    val isCompleted: Boolean = false,
    val createdAt: Date = Date()
) 