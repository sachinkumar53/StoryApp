package com.sachin.app.storyapp.ui.singup

import android.graphics.Bitmap
import android.net.Uri
import androidx.core.util.PatternsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sachin.app.storyapp.core.util.BitmapDecoder
import com.sachin.app.storyapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val bitmapDecoder: BitmapDecoder
) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(SignUpUiState())
    private val _resultFlow = MutableSharedFlow<SignUpResult>()

    val uiStateLiveData = _uiStateFlow.asLiveData()
    val resultFlow = _resultFlow.asSharedFlow()

    fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.OnNameChanged -> _uiStateFlow.update {
                it.copy(name = event.name, nameError = null)
            }

            is SignUpEvent.OnEmailChanged -> _uiStateFlow.update {
                it.copy(email = event.email, emailError = null)
            }

            is SignUpEvent.OnPasswordChanged -> _uiStateFlow.update {
                it.copy(password = event.password, passwordError = null)
            }

            is SignUpEvent.OnConfirmPasswordChanged -> _uiStateFlow.update {
                it.copy(confirmPassword = event.password, confirmPasswordError = null)
            }

            is SignUpEvent.OnProfilePictureChanged -> viewModelScope.launch {
                _uiStateFlow.update { it.copy(isLoadingProfileImage = true) }
                val bitmap = bitmapDecoder.decodeSampledBitmapResource(event.uri)
                _uiStateFlow.update {
                    it.copy(
                        isLoadingProfileImage = false,
                        profileBitmap = bitmap
                    )
                }
            }

            is SignUpEvent.OnSignUpClick -> signup()
        }
    }

    private fun signup() {
        if (!validate()) return
        viewModelScope.launch {
            _uiStateFlow.update { it.copy(isLoading = true) }

            val result = repository.signUpWithEmailAndPassword(
                profileImage = _uiStateFlow.value.profileBitmap,
                name = _uiStateFlow.value.name,
                email = _uiStateFlow.value.email,
                password = _uiStateFlow.value.password
            )

            _uiStateFlow.update { it.copy(isLoading = false) }

            if (result.isSuccess) {
                _resultFlow.emit(SignUpResult.Success(result.getOrElse { "" }))
            } else {
                _resultFlow.emit(
                    SignUpResult.Error(
                        result.exceptionOrNull()?.message ?: "Something went wrong!"
                    )
                )
            }
        }
    }

    private fun validate(): Boolean = with(_uiStateFlow.value) {

        if (name.isBlank()) {
            _uiStateFlow.update { it.copy(nameError = "Please enter your name.") }
            return false
        }
        if (!name.all { it.isLetter() || it.isWhitespace() }) {
            _uiStateFlow.update { it.copy(nameError = "Please enter a valid name.") }
            return false
        }
        if (email.isBlank()) {
            _uiStateFlow.update { it.copy(emailError = "Please enter your email id.") }
            return false
        }

        if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiStateFlow.update { it.copy(emailError = "Please enter a valid email id.") }
            return false
        }

        if (password.isBlank()) {
            _uiStateFlow.update { it.copy(passwordError = "Please enter a password.") }
            return false
        }
        if (password.length < 8) {
            _uiStateFlow.update { it.copy(passwordError = "Password must be at least 8 characters long.") }
            return false
        }

        if (password.length > 12) {
            _uiStateFlow.update { it.copy(passwordError = "Password must be at most 12 characters long.") }
            return false
        }

        if (confirmPassword.isBlank()) {
            _uiStateFlow.update { it.copy(confirmPasswordError = "Please confirm your password.") }
            return false
        }

        if (confirmPassword != password) {
            _uiStateFlow.update { it.copy(confirmPasswordError = "Passwords do not match.") }
            return false
        }
        return true
    }


}


sealed interface SignUpEvent {
    data class OnNameChanged(val name: String) : SignUpEvent
    data class OnEmailChanged(val email: String) : SignUpEvent
    data class OnPasswordChanged(val password: String) : SignUpEvent
    data class OnConfirmPasswordChanged(val password: String) : SignUpEvent
    data class OnProfilePictureChanged(val uri: Uri?) : SignUpEvent
    object OnSignUpClick : SignUpEvent
}

data class SignUpUiState(
    val profileBitmap: Bitmap? = null,
    val isLoadingProfileImage: Boolean = false,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false
)

sealed interface SignUpResult {
    data class Success(val message: String) : SignUpResult
    data class Error(val message: String) : SignUpResult
}