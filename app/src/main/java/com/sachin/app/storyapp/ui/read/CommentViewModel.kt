package com.sachin.app.storyapp.ui.read

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sachin.app.storyapp.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val repository: ReviewRepository,
    private val auth: FirebaseAuth,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val storyId: String = savedStateHandle["story_id"]!!
    val currentUser get() = auth.currentUser
    val comments = repository.getComments(storyId).stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun deleteComment(commentId: String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            repository.deleteComment(storyId, commentId)
        }
    }

    fun postComment(commentText: String, onComplete: () -> Unit) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
            onComplete()
        }) {
            repository.postComment(storyId, commentText)
            onComplete()
        }
    }

}
