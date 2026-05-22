package com.example.cinelog.domain.model

import com.example.cinelog.data.model.Movie

data class HomeState(
    val featuredMovie: Movie? = null,
    val horrorMovies: List<Movie> = emptyList(),
    val romanceMovies: List<Movie> = emptyList(),
    val animationMovies: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    val featuredSeries: Movie? = null,
    val horrorSeries: List<Movie> = emptyList(),
    val romanceSeries: List<Movie> = emptyList(),
    val animationSeries: List<Movie> = emptyList()
)