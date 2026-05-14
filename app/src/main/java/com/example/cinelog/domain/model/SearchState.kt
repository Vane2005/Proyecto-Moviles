package com.example.cinelog.domain.model

import com.example.cinelog.data.model.Movie

data class SearchState(
    val query: String = "",
    val results: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
