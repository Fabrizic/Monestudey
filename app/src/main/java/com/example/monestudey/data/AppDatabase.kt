package com.example.monestudey.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.monestudey.data.dao.*
import com.example.monestudey.data.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Transaction::class,
        Category::class,
        Budget::class,
        SavingsGoal::class,
        FinancialReminder::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun savingsGoalDao(): SavingsGoalDao
    abstract fun financialReminderDao(): FinancialReminderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "monestudey_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Inicializar categorías predeterminadas
                        CoroutineScope(Dispatchers.IO).launch {
                            val categoryDao = getDatabase(context).categoryDao()
                            
                            // Categorías de ingresos
                            categoryDao.insertCategory(Category(
                                name = "Sueldo",
                                type = CategoryType.INCOME,
                                icon = "💰"
                            ))
                            categoryDao.insertCategory(Category(
                                name = "Beca",
                                type = CategoryType.INCOME,
                                icon = "🎓"
                            ))
                            categoryDao.insertCategory(Category(
                                name = "Préstamo",
                                type = CategoryType.INCOME,
                                icon = "💵"
                            ))
                            categoryDao.insertCategory(Category(
                                name = "Otros Ingresos",
                                type = CategoryType.INCOME,
                                icon = "📈"
                            ))

                            // Categorías de gastos
                            categoryDao.insertCategory(Category(
                                name = "Alimentación",
                                type = CategoryType.EXPENSE,
                                icon = "🍔"
                            ))
                            categoryDao.insertCategory(Category(
                                name = "Transporte",
                                type = CategoryType.EXPENSE,
                                icon = "🚌"
                            ))
                            categoryDao.insertCategory(Category(
                                name = "Vivienda",
                                type = CategoryType.EXPENSE,
                                icon = "🏠"
                            ))
                            categoryDao.insertCategory(Category(
                                name = "Educación",
                                type = CategoryType.EXPENSE,
                                icon = "📚"
                            ))
                            categoryDao.insertCategory(Category(
                                name = "Entretenimiento",
                                type = CategoryType.EXPENSE,
                                icon = "🎮"
                            ))
                            categoryDao.insertCategory(Category(
                                name = "Salud",
                                type = CategoryType.EXPENSE,
                                icon = "⚕️"
                            ))
                            categoryDao.insertCategory(Category(
                                name = "Ropa",
                                type = CategoryType.EXPENSE,
                                icon = "👕"
                            ))
                            categoryDao.insertCategory(Category(
                                name = "Otros Gastos",
                                type = CategoryType.EXPENSE,
                                icon = "📉"
                            ))
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 