package com.example.cinelog.domain.model

import com.example.cinelog.data.model.MovieItem

data class User (
    val uid: String = "",
    val nombre: String = "",
    val edad: Int = 0,
    val email: String = "",
    val watchlist: List<MovieItem> = emptyList(),
    val favoritas: List<MovieItem> = emptyList(),
    val yaVisto: List<MovieItem> = emptyList()
)