package com.example.triply.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.triply.data.local.Expense
import com.example.triply.data.local.Trip
import com.example.triply.data.repository.TriplyRepository
import kotlinx.coroutines.flow.*

data class TripDetailsUiState(
    val trip: Trip? = null,
    val totalExpenses: Double = 0.0,
    val remainingBudget: Double = 0.0
)

class TripDetailsViewModel(private val repository: TriplyRepository, private val tripId: Long) : ViewModel() {
    
    private val _trip = repository.getTripStream(tripId)
    private val _expenses = repository.getExpensesStream(tripId)

    val uiState: StateFlow<TripDetailsUiState> = combine(_trip, _expenses) { trip, expenses ->
        val total = expenses.sumOf { it.amount }
        TripDetailsUiState(
            trip = trip,
            totalExpenses = total,
            remainingBudget = (trip?.budget ?: 0.0) - total
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TripDetailsUiState()
    )
}

class TripDetailsViewModelFactory(
    private val repository: TriplyRepository,
    private val tripId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TripDetailsViewModel(repository, tripId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
