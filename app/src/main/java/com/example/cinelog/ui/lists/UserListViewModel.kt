package com.example.cinelog.ui.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinelog.data.model.MovieItem
import com.example.cinelog.data.repository.UserListRepository
import com.example.cinelog.domain.model.ListType
import com.example.cinelog.domain.model.UserListState
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserListViewModel(
    private val userListRepository: UserListRepository = UserListRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserListState())
    val uiState: StateFlow<UserListState> = _uiState.asStateFlow()

    init {
        loadAllLists()
    }

    fun loadAllLists(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            if (isRefreshing) {
                _uiState.update { it.copy(isRefreshing = true) }
            } else {
                _uiState.update { it.copy(isLoading = true) }
            }

            val watchlistDeferred = async { userListRepository.getMoviesFromList(ListType.WATCHLIST) }
            val favoritasDeferred = async { userListRepository.getMoviesFromList(ListType.FAVORITAS) }
            val yaVistoDeferred = async { userListRepository.getMoviesFromList(ListType.YA_VISTO) }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    isRefreshing = false,
                    watchlist = watchlistDeferred.await().getOrDefault(emptyList()),
                    favoritas = favoritasDeferred.await().getOrDefault(emptyList()),
                    yaVisto = yaVistoDeferred.await().getOrDefault(emptyList())
                )
            }
        }
    }

    fun addToList(movie: MovieItem, listType: ListType) {
        viewModelScope.launch {
            userListRepository.addMovieToList(movie, listType)
                .onSuccess {
                    if (listType == ListType.FAVORITAS) {
                        userListRepository.addMovieToList(
                            movie,
                            ListType.YA_VISTO
                        )
                    }
                    loadAllLists()
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            errorMessage = e.message
                        )
                    }
                }
        }
    }

    fun removeFromList(movie: MovieItem, listType: ListType) {
        viewModelScope.launch {
            userListRepository.removeMovieFromList(movie, listType)
                .onSuccess { loadAllLists() }
                .onFailure { e ->
                    _uiState.update { it.copy(errorMessage = e.message) }
                }
        }
    }
}