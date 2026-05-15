package com.example.cinelog.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinelog.data.repository.MovieRepository
import com.example.cinelog.domain.model.SearchState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    private var searchJob: Job? = null

    fun onQueryChange(query: String) {
        searchJob?.cancel()
        _uiState.update { 
            it.copy(
                query = query, 
                errorMessage = null, 
                results = emptyList(), 
                currentPage = 1,
                totalPages = 1
            ) 
        }
        
        if (query.isBlank()) {
            _uiState.update { it.copy(isLoading = false) }
            return
        }
        
        search(query)
    }

    private fun search(query: String) {
        searchJob = viewModelScope.launch {
            delay(300)
            _uiState.update { it.copy(isLoading = true) }
            movieRepository.searchMovies(query, page = 1)
                .onSuccess { response ->
                    _uiState.update { it.copy(
                        isLoading = false, 
                        results = response.results,
                        currentPage = response.page,
                        totalPages = response.totalPages
                    ) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
        }
    }

    fun loadNextPage() {
        val currentState = _uiState.value
        if (currentState.isLoading || currentState.isLoadingMore || currentState.currentPage >= currentState.totalPages) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            val nextPage = currentState.currentPage + 1
            movieRepository.searchMovies(currentState.query, page = nextPage)
                .onSuccess { response ->
                    _uiState.update { it.copy(
                        isLoadingMore = false,
                        results = it.results + response.results,
                        currentPage = response.page,
                        totalPages = response.totalPages
                    ) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoadingMore = false, errorMessage = e.message) }
                }
        }
    }
}
