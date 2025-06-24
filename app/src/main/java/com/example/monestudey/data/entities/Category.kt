package com.example.monestudey.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: CategoryType,
    val icon: String? = null,
    val color: Int? = null
)

enum class CategoryType {
    INCOME,
    EXPENSE
} 