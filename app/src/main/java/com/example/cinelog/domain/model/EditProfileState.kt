package com.example.cinelog.domain.model


data class EditProfileState(
    val nombre: String = "",
    val edad: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSaveSuccessful: Boolean = false,
    val errorMessage: String? = null
)