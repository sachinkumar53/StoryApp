package com.sachin.app.storyapp.ui.read

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sachin.app.storyapp.data.repository.DetailRepository
import com.sachin.app.storyapp.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReadViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: DetailRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {
    val storyId: String = savedStateHandle["story_id"]!!
    private val _uiStateFlow = MutableStateFlow<ReadUiState>(ReadUiState.Loading)
    val uiState = _uiStateFlow.asLiveData()
    private val _ratingUiStateFlow = MutableStateFlow(RatingUiState())
    val ratingUiState = _ratingUiStateFlow.asLiveData()

    val topCommentFlow = reviewRepository.getTopComment(storyId).asLiveData()

    val currentUser = FirebaseAuth.getInstance().currentUser


    init {
        viewModelScope.launch {
            val story = repository.getStory(storyId)
            _uiStateFlow.update {
                ReadUiState.Success(
                    title = story.title,
                    content = story.content,
                    authorName = story.authorName,
                    authorImageUrl = story.authorPhotoUrl
                )
            }
        }

        combine(
            reviewRepository.getMyRating(storyId),
            reviewRepository.getTotalRating(storyId)
        ) { myRating, (totalRating, count) ->
            _ratingUiStateFlow.update {
                it.copy(
                    myRating = myRating,
                    totalRating = totalRating,
                    ratingCount = count
                )
            }
        }.launchIn(viewModelScope)
    }

    fun deleteRating() {
        viewModelScope.launch {
            reviewRepository.deleteRating(storyId)
        }
    }

}

sealed interface ReadUiState {
    object Loading : ReadUiState
    data class Success(
        val title: String,
        val content: String,
        val authorName: String,
        val authorImageUrl: String
    ) : ReadUiState
}

data class RatingUiState(
    val myRating: Int = 0,
    val totalRating: Float = 0f,
    val ratingCount: Int = 0
)