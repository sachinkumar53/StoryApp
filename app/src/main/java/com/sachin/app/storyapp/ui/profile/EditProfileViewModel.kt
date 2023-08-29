package com.sachin.app.storyapp.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sachin.app.storyapp.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            repository.getProfile().firstOrNull()?.let { profile ->
                _uiState.update { state ->
                    state.copy(
                        name = profile.name,
                        photoUrl = profile.photoUrl?.let { Uri.parse(it) },
                        email = profile.email
                    )
                }
            }
        }
    }

    fun onPhotoUriChanged(uri: Uri?) {
        _uiState.update { it.copy(photoUrl = uri, isImageChanged = true) }
    }

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(name = name, nameError = null) }
    }

    fun update(onComplete: (Boolean) -> Unit) {
        if (!validate()) {
            onComplete(false)
            return
        }

        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
            onComplete(false)
        }) {
            repository.updateUserDetails(
                name = _uiState.value.name,
                photoUri = _uiState.value.photoUrl,
                localImage = _uiState.value.isImageChanged
            )
            onComplete(true)
        }
    }


    private fun validate(): Boolean {
        if (_uiState.value.name.isBlank()) {
            _uiState.update { it.copy(nameError = "Please enter your name.") }
            return false
        }
        return true
    }

}

data class EditProfileUiState(
    val name: String = "",
    val photoUrl: Uri? = null,
    val email: String = "",
    val isLoading: Boolean = false,
    val nameError: String? = null,
    val isImageChanged: Boolean = false
)