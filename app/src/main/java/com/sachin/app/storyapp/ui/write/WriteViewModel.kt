package com.sachin.app.storyapp.ui.write

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sachin.app.storyapp.data.repository.PublicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WriteViewModel @Inject constructor(
    private val repository: PublicationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WriteUiState())
    val uiString = _uiState.asLiveData()

    fun onCoverSelected(bitmap: Bitmap?) {
        _uiState.update { it.copy(cover = bitmap, coverError = null) }
    }

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value, titleError = null) }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value, descriptionError = null) }
    }

    fun onGenreChanged(value: String) {
        _uiState.update { it.copy(genre = value, genreError = null) }
    }

    fun onContentChange(value: String) {
        _uiState.update { it.copy(content = value) }
    }

    private fun publish(onComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isPublishing = true) }
            val cover = _uiState.value.cover!!
            val (title, description, content, genre) = _uiState.value.run {
                arrayOf(title, description, content, genre)
            }
            repository.publish(
                cover = cover,
                title = title,
                description = description,
                content = content,
                genre = genre
            )
            onComplete()
        }
    }

    fun onPublishClick(onComplete: () -> Unit) {
        publish(onComplete)
    }

    fun validate(/*checkContent: Boolean*/): Boolean = _uiState.value.run {
        if (cover == null) {
            _uiState.update { it.copy(coverError = "Please add a cover image") }
            return false
        }
        if (title.isBlank()) {
            _uiState.update { it.copy(titleError = "Please enter a title") }
            return false
        }
        if (description.isBlank()) {
            _uiState.update { it.copy(descriptionError = "Please enter a short description") }
            return false
        }
        if (genre.isBlank()) {
            _uiState.update { it.copy(genreError = "Please select at least one genre") }
            return false
        }
        /*if (checkContent && content.isBlank()) {
            _uiState.update { it.copy(contentError = "Please write story") }
            return false
        }*/
        return true
    }
}

data class WriteUiState(
    val cover: Bitmap? = null,
    val title: String = "",
    val description: String = "",
    val genre: String = "",
    val content: String = "",
    val isPublishing: Boolean = false,
    val coverError: String? = null,
    val titleError: String? = null,
    val descriptionError: String? = null,
    val genreError: String? = null,
//    val contentError: String? = null
)