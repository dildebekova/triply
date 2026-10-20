package com.example.triply.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.triply.data.local.*
import com.example.triply.data.network.WeatherData
import com.example.triply.data.network.WeatherService
import com.example.triply.data.repository.TriplyRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TripDetailsUiState(
    val trip: Trip? = null,
    val totalExpenses: Double = 0.0,
    val remainingBudget: Double = 0.0,
    val packingProgress: Float = 0f,
    val packedItemsCount: Int = 0,
    val totalItemsCount: Int = 0,
    val scheduleItems: List<ScheduleItem> = emptyList(),
    val places: List<Place> = emptyList(),
    val weather: WeatherData? = null,
    val isWeatherLoading: Boolean = false,
    val currency: String = "$"
)

class TripDetailsViewModel(private val repository: TriplyRepository, private val tripId: Long) : ViewModel() {
    
    private val weatherService = WeatherService()
    private val _weather = MutableStateFlow<WeatherData?>(null)
    private val _isWeatherLoading = MutableStateFlow(false)

    private val _trip = repository.getTripStream(tripId)
    private val _expenses = repository.getExpensesStream(tripId)
    private val _packingItems = repository.getPackingItemsStream(tripId)
    private val _schedule = repository.getScheduleStream(tripId)
    private val _places = repository.getPlacesStream(tripId)
    private val _user = repository.getUserStream()

    val uiState: StateFlow<TripDetailsUiState> = combine(
        _trip, _expenses, _packingItems, _schedule, _places, _weather, _isWeatherLoading, _user
    ) { flows ->
        val trip = flows[0] as Trip?
        @Suppress("UNCHECKED_CAST")
        val expenses = flows[1] as List<Expense>
        @Suppress("UNCHECKED_CAST")
        val items = flows[2] as List<PackingItem>
        @Suppress("UNCHECKED_CAST")
        val schedule = flows[3] as List<ScheduleItem>
        @Suppress("UNCHECKED_CAST")
        val places = flows[4] as List<Place>
        val weather = flows[5] as WeatherData?
        val isLoading = flows[6] as Boolean
        val user = flows[7] as User?

        val total = expenses.sumOf { it.amount }
        val totalItems = items.size
        val packedItems = items.count { it.isChecked }
        
        // Trigger weather fetch when trip location is known
        if (trip != null && weather == null && !isLoading) {
            fetchWeather(trip.location)
        }

        TripDetailsUiState(
            trip = trip,
            totalExpenses = total,
            remainingBudget = (trip?.budget ?: 0.0) - total,
            packingProgress = if (totalItems > 0) packedItems.toFloat() / totalItems else 0f,
            packedItemsCount = packedItems,
            totalItemsCount = totalItems,
            scheduleItems = schedule,
            places = places,
            weather = weather,
            isWeatherLoading = isLoading,
            currency = user?.currency ?: "$"
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TripDetailsUiState()
    )

    private fun fetchWeather(location: String) {
        viewModelScope.launch {
            _isWeatherLoading.value = true
            try {
                _weather.value = weatherService.getWeather(location)
            } finally {
                _isWeatherLoading.value = false
            }
        }
    }
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
