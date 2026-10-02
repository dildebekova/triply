package com.example.triply.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.triply.data.local.Photo
import com.example.triply.data.repository.TriplyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PhotosViewModel(private val repository: TriplyRepository, private val tripId: Long) : ViewModel() {
    val photosState: StateFlow<List<Photo>> = repository.getPhotosStream(tripId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addPhoto(uri: String) {
        viewModelScope.launch {
            repository.insertPhoto(Photo(tripId = tripId, uri = uri))
        }
    }

    fun deletePhoto(photo: Photo) {
        viewModelScope.launch {
            repository.deletePhoto(photo)
        }
    }
}

class PhotosViewModelFactory(private val repository: TriplyRepository, private val tripId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhotosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PhotosViewModel(repository, tripId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
