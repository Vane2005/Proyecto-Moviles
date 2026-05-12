package com.example.cinelog.ui.changePassword


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinelog.data.repository.AuthRepository
import com.example.cinelog.domain.model.ChangePasswordState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChangePasswordViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePasswordState())
    val uiState: StateFlow<ChangePasswordState> = _uiState.asStateFlow()

    fun onCurrentPasswordChange(value: String) {
        _uiState.update {
            it.copy(
                currentPassword = value,
                errorMessage = null
            )
        }
    }

    fun onNewPasswordChange(value: String) {
        _uiState.update {
            it.copy(
                newPassword = value,
                errorMessage = null
            )
        }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update {
            it.copy(
                confirmPassword = value,
                errorMessage = null
            )
        }
    }

    fun onChangePasswordClick() {
        val state = _uiState.value

        if (
            state.currentPassword.isBlank() ||
            state.newPassword.isBlank() ||
            state.confirmPassword.isBlank()
        ) {
            _uiState.update {
                it.copy(errorMessage = "Todos los campos son obligatorios")
            }
            return
        }

        if (state.newPassword.length < 6) {
            _uiState.update {
                it.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres")
            }
            return
        }

        if (state.newPassword != state.confirmPassword) {
            _uiState.update {
                it.copy(errorMessage = "Las contraseñas no coinciden")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            authRepository.changePassword(
                currentPassword = state.currentPassword,
                newPassword = state.newPassword
            ).onSuccess {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                }

            }.onFailure { e ->

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    fun onSuccessHandled() {
        _uiState.update {
            it.copy(isSuccess = false)
        }
    }
}