package com.example.cinelog.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinelog.data.repository.AuthRepository
import com.example.cinelog.domain.model.RegisterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterState())
    val uiState: StateFlow<RegisterState> = _uiState.asStateFlow()

    fun onNombreChange(value: String) {
        _uiState.update { it.copy(nombre = value, errorMessage = null) }
    }

    fun onEdadChange(value: String) {
        _uiState.update { it.copy(edad = value, errorMessage = null) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value, errorMessage = null) }
    }

    fun onRegisterClick() {
        val state = _uiState.value

        // Validaciones
        if (state.nombre.isBlank() || state.edad.isBlank() ||
            state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Completa todos los campos") }
            return
        }

        val edad = state.edad.toIntOrNull()
        if (edad == null || edad <= 0) {
            _uiState.update { it.copy(errorMessage = "Ingresa una edad valida") }
            return
        }

        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Las contrasenas no coinciden") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            authRepository.register(state.nombre, edad, state.email, state.password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isRegisterSuccessful = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
        }
    }
}