package com.example.triply.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.triply.TriplyApplication
import com.example.triply.data.local.Expense
import com.example.triply.ui.components.FeminineTextField
import com.example.triply.ui.theme.PinkPrimary
import com.example.triply.ui.theme.PinkSecondary
import com.example.triply.ui.theme.PinkTertiary
import com.example.triply.ui.viewmodels.ExpensesViewModel
import com.example.triply.ui.viewmodels.ExpensesViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    tripId: Long,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = (context.applicationContext as TriplyApplication).repository
    val viewModel: ExpensesViewModel = viewModel(factory = ExpensesViewModelFactory(repository, tripId))
    val expenses by viewModel.expensesState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val totalExpenses = expenses.sumOf { it.amount }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text("Trip Expenses", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PinkPrimary)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PinkPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Summary Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(PinkPrimary, PinkSecondary)
                        )
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total Spent", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)
                    Text(
                        text = "$${String.format(Locale.getDefault(), "%.2f", totalExpenses)}",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            if (expenses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No expenses yet. \uD83D\uDCB8", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(expenses, key = { it.id }) { expense ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + expandVertically()
                        ) {
                            ExpenseItemRow(expense = expense, onDelete = { viewModel.deleteExpense(expense) })
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddExpenseDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { amount, cat, desc ->
                    viewModel.addExpense(amount, cat, desc)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun ExpenseItemRow(expense: Expense, onDelete: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(PinkTertiary.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(20.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = expense.category, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                Text(text = dateFormat.format(Date(expense.date)), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${String.format(Locale.getDefault(), "%.2f", expense.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PinkPrimary
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp).padding(top = 4.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = PinkSecondary, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun AddExpenseDialog(onDismiss: () -> Unit, onConfirm: (Double, String, String) -> Unit) {
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White,
        title = { Text("Add Expense", fontWeight = FontWeight.Bold, color = PinkPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FeminineTextField(value = amount, onValueChange = { amount = it }, label = "Amount", placeholder = "0.00", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                FeminineTextField(value = category, onValueChange = { category = it }, label = "Category", placeholder = "Food, Flight, Hotel...")
                FeminineTextField(value = desc, onValueChange = { desc = it }, label = "Note", placeholder = "Lunch at the cafe")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val value = amount.toDoubleOrNull() ?: 0.0
                    onConfirm(value, category, desc)
                },
                enabled = amount.isNotBlank() && category.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Gray) }
        }
    )
}
