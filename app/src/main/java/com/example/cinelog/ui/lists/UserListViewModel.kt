package com.example.cinelog.ui.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinelog.domain.model.ListType
import com.example.cinelog.data.model.MovieItem
import com.example.cinelog.data.repository.UserListRepository
import com.example.cinelog.domain.model.UserListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserListViewModel(
    private val userListRepository: UserListRepository = UserListRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserListState())
    val uiState: StateFlow<UserListState> = _uiState.asStateFlow()

    /*
    Cargar todas las listas del usuario
    init {
        loadAllLists()
    }*/



    /*
    fun addToList(movie: MovieItem, listType: ListType) {

        viewModelScope.launch {
            userListRepository.addMovieToList(movie, listType)
                .onSuccess {
                    loadAllLists()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(errorMessage = e.message) }
                }
        }
    }
    */



}