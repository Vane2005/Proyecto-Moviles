package com.example.cinelog.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinelog.data.repository.MovieRepository
import com.example.cinelog.domain.model.SearchState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val movieRepository: MovieRepository = MovieRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchState())
    val uiState: StateFlow<SearchState> = _uiState.asStateFlow()

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query, errorMessage = null) }
        if (query.isBlank()) {
            _uiState.update { it.copy(results = emptyList()) }
            return
        }
        search(query)
    }

    private fun search(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            movieRepository.searchMovies(query)
                .onSuccess { movies ->
                    _uiState.update { it.copy(isLoading = false, results = movies) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
        }
    }
}