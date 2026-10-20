package com.example.triply.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.triply.data.local.Expense
import com.example.triply.data.repository.TriplyRepository
import kotlinx.coroutines.flow.*

data class StatisticsUiState(
    val totalSpent: Double = 0.0,
    val categoryBreakdown: Map<String, Double> = emptyMap(),
    val expenseCount: Int = 0,
    val currency: String = "$"
)

class StatisticsViewModel(private val repository: TriplyRepository) : ViewModel() {
    
    val uiState: StateFlow<StatisticsUiState> = combine(
        repository.getAllExpensesStream(),
        repository.getUserStream()
    ) { expenses, user ->
        StatisticsUiState(
            totalSpent = expenses.sumOf { it.amount },
            categoryBreakdown = expenses.groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } },
            expenseCount = expenses.size,
            currency = user?.currency ?: "$"
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatisticsUiState()
    )
}

class StatisticsViewModelFactory(private val repository: TriplyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatisticsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
