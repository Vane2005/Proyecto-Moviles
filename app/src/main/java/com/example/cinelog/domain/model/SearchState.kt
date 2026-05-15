package com.example.cinelog.domain.model

import com.example.cinelog.data.model.Movie

data class SearchState(
    val query: String = "",
    val results: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val errorMessage: String? = null
)
