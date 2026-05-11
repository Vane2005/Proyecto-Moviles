package com.example.cinelog.domain.model

data class ReviewState(
    val calificacion: Int = 0,
    val reseña: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSaveSuccessful: Boolean = false
)