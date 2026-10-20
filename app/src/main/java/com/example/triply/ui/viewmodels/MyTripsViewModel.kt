package com.example.triply.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.triply.data.local.Trip
import com.example.triply.data.repository.TriplyRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MyTripsViewModel(private val repository: TriplyRepository) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Реактивный поток данных: список фильтруется автоматически при вводе в поиск
    val tripsState: StateFlow<List<Trip>> = combine(
        repository.getAllTripsStream(),
        _searchQuery
    ) { trips, query ->
        if (query.isBlank()) {
            trips
        } else {
            trips.filter { 
                it.title.contains(query, ignoreCase = true) || 
                it.location.contains(query, ignoreCase = true) 
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            repository.deleteTrip(trip)
        }
    }

    // Вспомогательная функция для отображения статуса дней (Countdown)
    fun getDaysUntil(startDateMillis: Long): String {
        val diff = startDateMillis - System.currentTimeMillis()
        val days = TimeUnit.MILLISECONDS.toDays(diff)
        return when {
            days > 0 -> "Через $days дн."
            days == 0L -> "Сегодня!"
            else -> "В архиве"
        }
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
