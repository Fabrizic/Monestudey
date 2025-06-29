package com.example.monestudey.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index
import com.example.monestudey.data.entities.Category
import java.util.Date

@Entity(
    tableName = "transactions",
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
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val description: String,
    val categoryId: Long,
    val type: TransactionType,
    val date: Date,
    val isRecurring: Boolean = false,
    val recurrencePattern: String? = null,
    val createdAt: Date = Date()
)

enum class TransactionType {
    INCOME,
    EXPENSE
} 