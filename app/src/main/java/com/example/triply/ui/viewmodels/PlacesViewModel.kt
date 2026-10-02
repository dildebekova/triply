package com.example.triply.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.triply.data.local.Place
import com.example.triply.data.repository.TriplyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlacesViewModel(private val repository: TriplyRepository, private val tripId: Long) : ViewModel() {
    val placesState: StateFlow<List<Place>> = repository.getPlacesStream(tripId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addPlace(name: String, address: String, description: String) {
        viewModelScope.launch {
            repository.insertPlace(Place(tripId = tripId, name = name, address = address, description = description))
        }
    }

    fun deletePlace(place: Place) {
        viewModelScope.launch {
            repository.deletePlace(place)
        }
    }
}

class PlacesViewModelFactory(private val repository: TriplyRepository, private val tripId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlacesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlacesViewModel(repository, tripId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
