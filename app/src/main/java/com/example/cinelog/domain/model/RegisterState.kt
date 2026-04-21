package com.example.cinelog.domain.model

data class RegisterState(
    val nombre: String = "",
    val edad: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegisterSuccessful: Boolean = false
)