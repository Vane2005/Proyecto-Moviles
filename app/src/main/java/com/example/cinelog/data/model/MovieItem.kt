package com.example.cinelog.data.model

import com.google.firebase.firestore.PropertyName

data class MovieItem(
    val movieId: Int = 0,
    val titulo: String = "",
    val posterPath: String = "",
    @get:PropertyName("mediaType")
    @set:PropertyName("mediaType")
    var mediaType: String = "movie"
)
