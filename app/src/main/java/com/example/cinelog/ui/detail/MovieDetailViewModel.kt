package com.example.cinelog.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinelog.data.model.MovieDetail
import com.example.cinelog.data.repository.MovieRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MovieDetailUiState(
    val movie: MovieDetail? = null,
    val director: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showAddOptions: Boolean = false
)

class MovieDetailViewModel(
    private val movieRepository: MovieRepository = MovieRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(MovieDetailUiState())
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    fun loadMovieDetail(movieId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, movie = null, director = null) }
            
            val detailDeferred = async { movieRepository.getMovieDetail(movieId) }
            val creditsDeferred = async { movieRepository.getMovieCredits(movieId) }

            val detailResult = detailDeferred.await()
            val creditsResult = creditsDeferred.await()

            detailResult.onSuccess { detail ->
                val crew = creditsResult.getOrNull()?.crew ?: emptyList()
                val directorMember = crew.find { it.job.trim().equals("Director", ignoreCase = true) }
                
                _uiState.update { it.copy(
                    isLoading = false, 
                    movie = detail,
                    director = directorMember?.name ?: "Desconocido"
                ) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun toggleAddOptions() {
        _uiState.update { it.copy(showAddOptions = !it.showAddOptions) }
    }

    fun dismissAddOptions() {
        _uiState.update { it.copy(showAddOptions = false) }
    }
}
