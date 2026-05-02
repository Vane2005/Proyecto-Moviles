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
import android.util.Patterns

class RegisterViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterState())
    val uiState: StateFlow<RegisterState> = _uiState.asStateFlow()

    private fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

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

        if (state.nombre.isBlank() || state.edad.isBlank() ||
            state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Completa todos los campos") }
            return
        }

        val email = state.email.trim()
        if (!isValidEmail(email)) {
            _uiState.update { it.copy(errorMessage = "Ingresa un correo válido") }
            return
        }

        if (state.password.length < 6) {
            _uiState.update { it.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres") }
            return
        }

        val edad = state.edad.toIntOrNull()
        if (edad == null || edad <= 0) {
            _uiState.update { it.copy(errorMessage = "Ingresa una edad válida") }
            return
        }

        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Las contraseñas no coinciden") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            authRepository.register(state.nombre, edad, state.email, state.password)
                .onSuccess {
                    // Cerramos sesión inmediatamente. 
                    // Ya no necesitamos delay porque la navegación es explícita en CinelogApp.
                    authRepository.signOut()
                    _uiState.update { it.copy(isLoading = false, isRegisterSuccessful = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
        }
    }
}
