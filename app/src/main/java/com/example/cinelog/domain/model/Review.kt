package com.example.cinelog.domain.model

data class Review(
    val movieId: Int = 0,
    val titulo: String = "",
    val posterPath: String = "",
    val calificacion: Int = 0,
    val reseña: String = "",
    val etiquetas: String = "",
    val mediaType: String = "movie"
)