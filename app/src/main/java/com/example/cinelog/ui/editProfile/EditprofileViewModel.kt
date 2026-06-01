package com.example.cinelog.ui.editProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinelog.data.repository.AuthRepository
import com.example.cinelog.domain.model.EditProfileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.util.Patterns

class EditProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileState())
    val uiState: StateFlow<EditProfileState> = _uiState.asStateFlow()

    // Regex de Kotlin puro en lugar de android.util.Patterns para compatibilidad con pruebas unitarias (JVM)
    private fun isValidEmail(email: String): Boolean =
        email.trim().matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))

    fun loadUser(nombre: String, edad: String, email: String) {
        _uiState.update { it.copy(nombre = nombre, edad = edad, email = email) }
    }

    fun onNombreChange(value: String) {
        _uiState.update { it.copy(nombre = value, errorMessage = null) }
    }

    fun onEdadChange(value: String) {
        _uiState.update { it.copy(edad = value, errorMessage = null) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onSaveClick() {
        val state = _uiState.value

        if (state.nombre.isBlank() || state.edad.isBlank() || state.email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Nombre, edad y correo son obligatorios") }
            return
        }

        val email = state.email.trim()
        if (!isValidEmail(email)) {
            _uiState.update { it.copy(errorMessage = "Ingresa un correo válido") }
            return
        }

        val edad = state.edad.toIntOrNull()
        if (edad == null || edad <= 0) {
            _uiState.update { it.copy(errorMessage = "Ingresa una edad válida") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            authRepository.updateProfile(state.nombre, edad, email)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isSaveSuccessful = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
        }
    }

    fun onSaveHandled() {
        _uiState.update { it.copy(isSaveSuccessful = false) }
    }
}