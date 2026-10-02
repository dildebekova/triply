package com.example.triply.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.triply.data.local.Note
import com.example.triply.data.repository.TriplyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(private val repository: TriplyRepository, private val tripId: Long) : ViewModel() {
    val notesState: StateFlow<List<Note>> = repository.getNotesStream(tripId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addNote(content: String) {
        viewModelScope.launch {
            repository.insertNote(
                Note(
                    tripId = tripId,
                    content = content,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }
}

class NotesViewModelFactory(private val repository: TriplyRepository, private val tripId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotesViewModel(repository, tripId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
