package com.sachin.app.storyapp.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sachin.app.storyapp.core.util.Constant
import com.sachin.app.storyapp.data.event.ResultEvent
import com.sachin.app.storyapp.data.model.StoryDetail
import com.sachin.app.storyapp.data.repository.DetailRepository
import com.sachin.app.storyapp.data.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: DetailRepository,
    private val libraryRepository: LibraryRepository
) : ViewModel() {
    private val storyId: String = savedStateHandle["story_id"]!!
    private val _uiState = MutableStateFlow<StoryDetailUiState>(StoryDetailUiState.Loading)
    val uiState = _uiState.asLiveData()
    private val _resultEventFlow = MutableSharedFlow<ResultEvent<String>>(extraBufferCapacity = 1)
    val resultEventFlow = _resultEventFlow.asSharedFlow()

    init {
        loadStoryDetail()
    }

    private fun loadStoryDetail() {
        viewModelScope.launch {
            _uiState.update { StoryDetailUiState.Loading }
            val result = repository.getStory(storyId)
            _uiState.update { StoryDetailUiState.Success(result) }
        }
    }

    fun addToLibrary() {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            _resultEventFlow.tryEmit(
                ResultEvent.Error(throwable.message ?: Constant.FALLBACK_ERROR_MESSAGE)
            )
        }) {
            libraryRepository.addToLibrary(storyId)
            _resultEventFlow.tryEmit(ResultEvent.Success("Story added to library!"))
        }
    }
}

sealed interface StoryDetailUiState {
    object Loading : StoryDetailUiState
    data class Success(val storyDetail: StoryDetail) : StoryDetailUiState
}