package com.sachin.app.storyapp.ui.publication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sachin.app.storyapp.data.model.StoryBasic
import com.sachin.app.storyapp.data.repository.PublicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "PublicationViewModel"

@HiltViewModel
class PublicationViewModel @Inject constructor(
    private val repository: PublicationRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PublicationUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        repository.getMyPublications().onEach { list ->
            _uiState.update { it.copy(data = list, isLoading = false) }
        }.launchIn(viewModelScope)
    }

    fun deleteStory(storyId: String, onComplete: () -> Unit) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
            onComplete()
        }) {
            repository.deleteStory(storyId)
            onComplete()
        }
    }
}

data class PublicationUiState(
    val data: List<StoryBasic> = emptyList(),
    val isLoading: Boolean = false
)