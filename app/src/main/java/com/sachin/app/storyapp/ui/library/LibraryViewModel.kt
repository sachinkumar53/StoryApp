package com.sachin.app.storyapp.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sachin.app.storyapp.data.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: LibraryRepository
) : ViewModel() {

    val itemsFlow = repository.getLibraryItems().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000),
        emptyList()
    )


    fun removeFromLibrary(storyId: String, onComplete: () -> Unit) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
            onComplete()
        }) {
            repository.removeFromLibrary(storyId)
            onComplete()
        }
    }
}