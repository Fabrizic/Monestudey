package com.example.monestudey.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.monestudey.data.Transaction
import com.example.monestudey.data.TransactionType
import com.example.monestudey.data.entities.Category
import com.example.monestudey.data.entities.CategoryType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseTrackerScreen(
    viewModel: ExpenseTrackerViewModel
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTransactionType by remember { mutableStateOf<TransactionType?>(null) }
    val transactions by viewModel.filteredTransactions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()
    val currentFilter by viewModel.filter.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Resumen de balance
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Balance Total",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "S/.${String.format("%.2f", totalIncome - totalExpense)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (totalIncome - totalExpense >= 0) 
                        Color(0xFF2E7D32) else Color(0xFFC62828)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Ingresos y Gastos
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Card de Ingresos
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E9) // Verde claro
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Ingresos",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF2E7D32) // Verde oscuro
                    )
                    Text(
                        "S/.${String.format("%.2f", totalIncome)}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Card de Gastos
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFEBEE) // Rojo claro
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Gastos",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFC62828) // Rojo oscuro
                    )
                    Text(
                        "S/.${String.format("%.2f", totalExpense)}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFFC62828),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botones de Agregar Ingreso y Gasto
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    selectedTransactionType = TransactionType.INCOME
                    showAddDialog = true
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32)
                )
            ) {
                Text("Agregar Ingreso")
            }
            Button(
                onClick = {
                    selectedTransactionType = TransactionType.EXPENSE
                    showAddDialog = true
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFC62828)
                )
            ) {
                Text("Agregar Gasto")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filtros de transacciones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TransactionFilter.values().forEach { filter ->
                FilterChip(
                    selected = currentFilter == filter,
                    onClick = { viewModel.setFilter(filter) },
                    label = {
                        Text(
                            when (filter) {
                                TransactionFilter.ALL -> "Todo"
                                TransactionFilter.INCOME -> "Ingresos"
                                TransactionFilter.EXPENSE -> "Gastos"
                            }
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de transacciones
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(transactions) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    category = categories.find { it.id == transaction.categoryId },
                    onDelete = { viewModel.deleteTransaction(transaction) }
                )
            }
        }
    }

    if (showAddDialog) {
        AddTransactionDialog(
            viewModel = viewModel,
            onDismiss = { 
                showAddDialog = false
                selectedTransactionType = null
            },
            onConfirm = { amount, description, categoryId, type, isRecurring, recurrencePattern ->
                viewModel.addTransaction(
                    amount = amount,
                    description = description,
                    categoryId = categoryId,
                    type = type,
                    isRecurring = isRecurring,
                    recurrencePattern = recurrencePattern
                )
                showAddDialog = false
                selectedTransactionType = null
            },
            type = selectedTransactionType ?: TransactionType.EXPENSE
        )
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    category: Category?,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("es", "ES")) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (transaction.type == TransactionType.INCOME)
                Color(0xFFE8F5E9) // Verde claro
            else
                Color(0xFFFFEBEE) // Rojo claro
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    transaction.description,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    category?.name ?: "Sin categoría",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    dateFormat.format(transaction.date),
                    style = MaterialTheme.typography.bodySmall
                )
                if (transaction.isRecurring) {
                    Text(
                        "Recurrente: ${transaction.recurrencePattern}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "S/.${String.format("%.2f", transaction.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (transaction.type == TransactionType.INCOME)
                        Color(0xFF2E7D32) // Verde oscuro
                    else
                        Color(0xFFC62828) // Rojo oscuro
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar transacción"
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    viewModel: ExpenseTrackerViewModel,
    onDismiss: () -> Unit,
    onConfirm: (Double, String, Long, TransactionType, Boolean, String?) -> Unit,
    type: TransactionType
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var isRecurring by remember { mutableStateOf(false) }
    var recurrencePattern by remember { mutableStateOf("") }

    val categories by viewModel.categories.collectAsState()
    val filteredCategories = categories.filter { 
        if (type == TransactionType.INCOME) it.type == CategoryType.INCOME
        else it.type == CategoryType.EXPENSE
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (type == TransactionType.INCOME) "Nuevo Ingreso" else "Nuevo Gasto") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Monto") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )

                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                ) {
                    OutlinedTextField(
                        value = filteredCategories.find { it.id == selectedCategoryId }?.name ?: "",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Categoría") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        filteredCategories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text("${category.icon} ${category.name}") },
                                onClick = { 
                                    selectedCategoryId = category.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isRecurring,
                        onCheckedChange = { isRecurring = it }
                    )
                    Text("Transacción Recurrente")
                }

                if (isRecurring) {
                    OutlinedTextField(
                        value = recurrencePattern,
                        onValueChange = { recurrencePattern = it },
                        label = { Text("Patrón de Recurrencia (ej: mensual, semanal)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountValue = amount.toDoubleOrNull() ?: 0.0
                    val categoryId = selectedCategoryId ?: return@Button
                    onConfirm(amountValue, description, categoryId, type, isRecurring, recurrencePattern.takeIf { it.isNotBlank() })
                },
                enabled = amount.isNotBlank() && description.isNotBlank() && selectedCategoryId != null
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}