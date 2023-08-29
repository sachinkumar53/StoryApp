package com.sachin.app.storyapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sachin.app.storyapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    private val _uiStateFlow = MutableStateFlow(LoginUiState())
    private val _resultFlow = MutableSharedFlow<LoginResult>()

    val uiStateLiveData = _uiStateFlow.asLiveData()
    val resultFlow = _resultFlow.asSharedFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnEmailChanged -> _uiStateFlow.update {
                it.copy(email = event.email, isEmailError = false)
            }

            is LoginEvent.OnPasswordChanged -> _uiStateFlow.update {
                it.copy(password = event.password, isPasswordError = false)
            }

            is LoginEvent.OnLoginClick -> login()
        }
    }

    private fun login() {
        if (!validate()) return

        viewModelScope.launch {
            _uiStateFlow.update { it.copy(isLoading = true) }
            val (email, password) = _uiStateFlow.value.run { Pair(email, password) }
            val result = repository.signInWithEmailAndPassword(email, password)
            _uiStateFlow.update { it.copy(isLoading = false) }
            if (result.isSuccess) {
                _resultFlow.emit(
                    LoginResult.Success(
                        userId = result.getOrElse { "" },
                        message = "Login successful"
                    )
                )
            } else {
                _resultFlow.emit(
                    LoginResult.Error(
                        result.exceptionOrNull()?.message ?: "Something went wrong!"
                    )
                )
            }
        }
    }

    private fun validate(): Boolean {
        if (_uiStateFlow.value.email.isBlank()) {
            _uiStateFlow.update { it.copy(isEmailError = true) }
            return false
        }
        if (_uiStateFlow.value.password.isBlank()) {
            _uiStateFlow.update { it.copy(isPasswordError = true) }
            return false
        }
        return true
    }


}

sealed interface LoginEvent {
    data class OnEmailChanged(val email: String) : LoginEvent
    data class OnPasswordChanged(val password: String) : LoginEvent
    object OnLoginClick : LoginEvent
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isEmailError: Boolean = false,
    val isPasswordError: Boolean = false,
    val isLoading: Boolean = false
)

sealed interface LoginResult {
    data class Success(
        val userId: String,
        val message: String
    ) : LoginResult

    data class Error(val message: String) : LoginResult
}