package com.example.cinelog.ui.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinelog.data.repository.MovieRepository
import com.example.cinelog.domain.model.HomeState
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val movieRepository: MovieRepository = MovieRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    init {
        loadHomeContent()
    }

    private fun loadHomeContent() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Lanzamos todas las peticiones en paralelo
            val trendingDeferred = async { movieRepository.getTrendingMovies() }
            val horrorDeferred = async { movieRepository.getMoviesByGenre(27) }
            val romanceDeferred = async { movieRepository.getMoviesByGenre(10749) }
            val animationDeferred = async { movieRepository.getMoviesByGenre(16) }

            val trending = trendingDeferred.await()
            val horror = horrorDeferred.await()
            val romance = romanceDeferred.await()
            val animation = animationDeferred.await()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    featuredMovie = trending.getOrNull()?.firstOrNull(),
                    horrorMovies = horror.getOrDefault(emptyList()),
                    romanceMovies = romance.getOrDefault(emptyList()),
                    animationMovies = animation.getOrDefault(emptyList())
                )
            }
        }
    }
}