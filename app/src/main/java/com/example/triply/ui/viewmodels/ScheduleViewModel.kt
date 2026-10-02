package com.example.triply.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.triply.data.local.ScheduleItem
import com.example.triply.data.repository.TriplyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScheduleViewModel(private val repository: TriplyRepository, private val tripId: Long) : ViewModel() {
    val scheduleState: StateFlow<List<ScheduleItem>> = repository.getScheduleStream(tripId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addScheduleItem(title: String, dateTime: Long, location: String, description: String) {
        viewModelScope.launch {
            repository.insertScheduleItem(
                ScheduleItem(
                    tripId = tripId,
                    title = title,
                    dateTime = dateTime,
                    location = location,
                    description = description
                )
            )
        }
    }

    fun deleteScheduleItem(item: ScheduleItem) {
        viewModelScope.launch {
            repository.deleteScheduleItem(item)
        }
    }
}

class ScheduleViewModelFactory(private val repository: TriplyRepository, private val tripId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScheduleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScheduleViewModel(repository, tripId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
