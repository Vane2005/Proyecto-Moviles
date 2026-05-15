package com.example.cinelog.domain.model

import com.google.firebase.firestore.Exclude

data class Review(
    @get:Exclude
    val documentId: String = "",
    val movieId: Int = 0,
    val titulo: String = "",
    val posterPath: String = "",
    val calificacion: Int = 0,
    val reseña: String = "",
    val etiquetas: String = "",
    val mediaType: String = "movie"
)