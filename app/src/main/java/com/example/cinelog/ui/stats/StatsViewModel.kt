package com.example.cinelog.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinelog.data.model.MovieItem
import com.example.cinelog.data.repository.MovieRepository
import com.example.cinelog.data.repository.UserListRepository
import com.example.cinelog.domain.model.ListType
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GenreCount(
    val name: String,
    val count: Int
)

data class StatsUiState(
    val topGenres: List<GenreCount> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class StatsViewModel(
    private val userListRepository: UserListRepository = UserListRepository(),
    private val movieRepository: MovieRepository = MovieRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState

    init {
        loadTopGenres()
    }

    private fun loadTopGenres() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val yaVistoResult = userListRepository.getMoviesFromList(ListType.YA_VISTO)
                val yaVisto = yaVistoResult.getOrDefault(emptyList())
                
                if (yaVisto.isEmpty()) {
                    _uiState.update { it.copy(isLoading = false, topGenres = emptyList()) }
                    return@launch
                }
                
                val topGenres = calculateTop3Genres(yaVisto)
                _uiState.update { it.copy(isLoading = false, topGenres = topGenres) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(isLoading = false, errorMessage = e.message) 
                }
            }
        }
    }

    private suspend fun calculateTop3Genres(yaVisto: List<MovieItem>): List<GenreCount> {
        val genreCounts = mutableMapOf<String, Int>()
        
        // Crear lista de Deferred para las operaciones asíncronas
        val detailJobs: List<Deferred<List<String>>> = yaVisto.map { item ->
            viewModelScope.async {
                try {
                    if (item.mediaType == "tv") {
                        val result = movieRepository.getTvDetail(item.movieId)
                        result.getOrNull()?.genres?.map { it.name } ?: emptyList()
                    } else {
                        val result = movieRepository.getMovieDetail(item.movieId)
                        result.getOrNull()?.genres?.map { it.name } ?: emptyList()
                    }
                } catch (e: Exception) {
                    emptyList()
                }
            }
        }
        
        // Esperar todos los resultados
        val allGenres = detailJobs.awaitAll()
        
        // Contar géneros
        allGenres.forEach { genres ->
            genres.forEach { genre ->
                genreCounts[genre] = (genreCounts[genre] ?: 0) + 1
            }
        }
        
        // Ordenar y tomar top 3
        return genreCounts.entries
            .sortedByDescending { it.value }
            .take(3)
            .map { (name, count) -> GenreCount(name, count) }
    }

    fun refresh() {
        loadTopGenres()
    }
}
