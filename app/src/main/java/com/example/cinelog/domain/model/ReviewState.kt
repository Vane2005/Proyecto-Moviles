package com.example.cinelog.domain.model

data class ReviewState(
    val reviews: List<Review> = emptyList(),
    val calificacion: Int = 0,
    val reseña: String = "",
    val etiquetas: String = "",
    val hasExistingReview: Boolean = false,
    val isLoadingReview: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSaveSuccessful: Boolean = false,
    val successToastMessage: String? = null
)