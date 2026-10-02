package com.example.triply.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.triply.data.local.Trip
import com.example.triply.data.repository.TriplyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MyTripsViewModel(private val repository: TriplyRepository) : ViewModel() {
    val tripsState: StateFlow<List<Trip>> = repository.getAllTripsStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    suspend fun deleteTrip(trip: Trip) {
        repository.deleteTrip(trip)
    }
}

class MyTripsViewModelFactory(private val repository: TriplyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyTripsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyTripsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
