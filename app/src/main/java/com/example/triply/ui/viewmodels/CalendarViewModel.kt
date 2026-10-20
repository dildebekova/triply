package com.example.triply.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.triply.data.local.ScheduleItem
import com.example.triply.data.local.Trip
import com.example.triply.data.repository.TriplyRepository
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*

data class CalendarUiState(
    val scheduleByDate: Map<String, List<ScheduleItem>> = emptyMap(),
    val trips: List<Trip> = emptyList(),
    val selectedTripId: Long? = null,
    val totalEvents: Int = 0,
    val isLoading: Boolean = false
)

class CalendarViewModel(private val repository: TriplyRepository) : ViewModel() {
    
    private val _selectedTripId = MutableStateFlow<Long?>(null)

    val uiState: StateFlow<CalendarUiState> = combine(
        repository.getAllScheduleItemsStream(),
        repository.getAllTripsStream(),
        _selectedTripId
    ) { items, trips, selectedId ->
        val filteredItems = if (selectedId != null) {
            items.filter { it.tripId == selectedId }
        } else {
            items
        }

        val dateFormat = SimpleDateFormat("EEEE, dd MMMM", Locale("ru"))
        // Sort items by time before grouping
        val sortedItems = filteredItems.sortedBy { it.dateTime }
        val grouped = sortedItems.groupBy { dateFormat.format(Date(it.dateTime)) }

        CalendarUiState(
            scheduleByDate = grouped,
            trips = trips,
            selectedTripId = selectedId,
            totalEvents = filteredItems.size,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalendarUiState(isLoading = true)
    )

    fun selectTrip(tripId: Long?) {
        _selectedTripId.value = tripId
    }
}

class CalendarViewModelFactory(private val repository: TriplyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalendarViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
