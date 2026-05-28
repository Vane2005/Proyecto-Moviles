package com.example.cinelog.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cinelog.data.repository.MovieRepository
import com.example.cinelog.data.repository.UserListRepository

class MovieDetailViewModelFactory(
    private val movieRepository: MovieRepository = MovieRepository(),
    private val userListRepository: UserListRepository = UserListRepository()
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieDetailViewModel::class.java)) {
            return MovieDetailViewModel(movieRepository, userListRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}