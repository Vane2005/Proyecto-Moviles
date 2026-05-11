package com.example.cinelog.data.model

data class MovieItem(
    val movieId: Int = 0,
    val titulo: String = "",
    val posterPath: String = "",
    val calificacion: Int? = null,
    val reseña: String? = null,
)