package com.sachin.app.storyapp.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sachin.app.storyapp.core.util.Constant
import com.sachin.app.storyapp.data.event.ResultEvent
import com.sachin.app.storyapp.data.model.Story
import com.sachin.app.storyapp.data.repository.CommunityRepository
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
class CommunityViewModel @Inject constructor(
    private val repository: CommunityRepository,
    private val libraryRepository: LibraryRepository
) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(CommunityUiState())
    val uiState = _uiStateFlow.asLiveData()
    private val _resultEventFlow = MutableSharedFlow<ResultEvent<String>>(extraBufferCapacity = 1)
    val resultEventFlow = _resultEventFlow.asSharedFlow()

    init {
        loadStory()
    }

    private fun loadStory() {
        viewModelScope.launch {
            _uiStateFlow.update { it.copy(isLoading = true) }
            val list = repository.getRecentPublications()
            _uiStateFlow.update { it.copy(isLoading = false, posts = list) }
        }
    }

    fun addToLibrary(story: Story) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            _resultEventFlow.tryEmit(
                ResultEvent.Error(throwable.message ?: Constant.FALLBACK_ERROR_MESSAGE)
            )
        }) {
            libraryRepository.addToLibrary(story.id)
            _resultEventFlow.tryEmit(ResultEvent.Success("Story added to library!"))
        }
    }
}

data class CommunityUiState(
    val posts: List<Story> = emptyList(),
    val isLoading: Boolean = false
)