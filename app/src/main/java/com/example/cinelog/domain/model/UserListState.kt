package com.example.cinelog.domain.model

import com.example.cinelog.data.model.MovieItem

data class UserListState(
    val watchlist: List<MovieItem> = emptyList(),
    val favoritas: List<MovieItem> = emptyList(),
    val yaVisto: List<MovieItem> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)