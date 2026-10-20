package com.example.triply.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.triply.data.local.PackingItem
import com.example.triply.data.repository.TriplyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PackingListViewModel(private val repository: TriplyRepository, private val tripId: Long) : ViewModel() {
    val itemsState: StateFlow<List<PackingItem>> = repository.getPackingItemsStream(tripId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addItem(name: String, category: String = "General") {
        viewModelScope.launch {
            repository.insertPackingItem(
                PackingItem(tripId = tripId, itemName = name, category = category)
            )
        }
    }

    fun addDefaultTemplate() {
        val baseItems = listOf(
            "Passport & ID", "Phone Charger", "Toothbrush", 
            "Power Bank", "First Aid Kit", "Tickets & Bookings", 
            "Cash & Cards", "Comfortable Shoes"
        )
        viewModelScope.launch {
            baseItems.forEach { name ->
                repository.insertPackingItem(PackingItem(tripId = tripId, itemName = name))
            }
        }
    }

    fun toggleItem(item: PackingItem) {
        viewModelScope.launch {
            repository.updatePackingItem(item.copy(isChecked = !item.isChecked))
        }
    }

    fun deleteItem(item: PackingItem) {
        viewModelScope.launch {
            repository.deletePackingItem(item)
        }
    }
}

class PackingListViewModelFactory(private val repository: TriplyRepository, private val tripId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PackingListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PackingListViewModel(repository, tripId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
