package com.example.triply.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.triply.data.local.Trip
import com.example.triply.data.repository.TriplyRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class TripUiState(
    val id: Long = 0,
    val title: String = "",
    val location: String = "",
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long = System.currentTimeMillis(),
    val budget: String = "",
    val description: String = ""
)

fun TripUiState.toTrip(): Trip = Trip(
    id = id,
    title = title,
    location = location,
    startDate = startDate,
    endDate = endDate,
    budget = budget.toDoubleOrNull() ?: 0.0,
    description = description
)

fun Trip.toTripUiState(): TripUiState = TripUiState(
    id = id,
    title = title,
    location = location,
    startDate = startDate,
    endDate = endDate,
    budget = budget.toString(),
    description = description
)

class CreateEditTripViewModel(private val repository: TriplyRepository) : ViewModel() {
    var tripUiState by mutableStateOf(TripUiState())
        private set

    fun updateUiState(newUiState: TripUiState) {
        tripUiState = newUiState
    }

    fun loadTrip(tripId: Long) {
        if (tripId != -1L) {
            viewModelScope.launch {
                tripUiState = repository.getTripStream(tripId)
                    .filterNotNull()
                    .first()
                    .toTripUiState()
            }
        }
    }

    suspend fun saveTrip() {
        if (tripUiState.id == 0L) {
            repository.insertTrip(tripUiState.toTrip())
        } else {
            repository.updateTrip(tripUiState.toTrip())
        }
    }
}

class CreateEditTripViewModelFactory(private val repository: TriplyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateEditTripViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateEditTripViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
