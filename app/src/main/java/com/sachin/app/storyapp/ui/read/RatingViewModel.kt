package com.sachin.app.storyapp.ui.read

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sachin.app.storyapp.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RatingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ReviewRepository
) : ViewModel() {
    val storyId: String = savedStateHandle["story_id"]!!

    fun rateStory(rating: Int, onComplete: () -> Unit) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
            onComplete()
        }) {
            repository.rateStory(storyId, rating)
            onComplete()
        }
    }
}