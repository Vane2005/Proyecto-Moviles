package com.example.cinelog.ui.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinelog.data.model.MovieItem
import com.example.cinelog.data.repository.ReviewRepository
import com.example.cinelog.data.repository.UserListRepository
import com.example.cinelog.domain.model.ListType
import com.example.cinelog.domain.model.Review
import com.example.cinelog.domain.model.ReviewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val reviewRepository: ReviewRepository = ReviewRepository(),
    private val userListRepository: UserListRepository = UserListRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewState())
    val uiState: StateFlow<ReviewState> = _uiState.asStateFlow()

    fun onCalificacionChange(value: Int) {
        _uiState.update { it.copy(calificacion = value, errorMessage = null) }
    }

    fun onReseñaChange(value: String) {
        _uiState.update { it.copy(reseña = value, errorMessage = null) }
    }

    fun onEtiquetasChange(value: String) {
        _uiState.update { it.copy(etiquetas = value) }
    }

    fun saveReview(movieId: Int, mediaType: String, titulo: String, posterPath: String) {
        val state = _uiState.value

        if (state.calificacion == 0) {
            _uiState.update { it.copy(errorMessage = "Selecciona una calificación") }
            return
        }

        if (state.reseña.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Escribe una reseña") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val review = Review(
                movieId = movieId,
                titulo = titulo,
                posterPath = posterPath,
                calificacion = state.calificacion,
                reseña = state.reseña,
                etiquetas = state.etiquetas,
                mediaType = mediaType
            )

            val reviewResult = reviewRepository.saveReview(review)
            
            if (reviewResult.isSuccess) {
                val movieItem = MovieItem(
                    movieId = movieId,
                    titulo = titulo,
                    posterPath = posterPath,
                    mediaType = mediaType
                )
                
                userListRepository.addMovieToList(movieItem, ListType.YA_VISTO)
                    .onSuccess {
                        _uiState.update { it.copy(isLoading = false, isSaveSuccessful = true) }
                    }
                    .onFailure {
                        _uiState.update { it.copy(isLoading = false, isSaveSuccessful = true) }
                    }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = reviewResult.exceptionOrNull()?.message) }
            }
        }
    }

    fun deleteReview(movieId: Int, mediaType: String) {
        viewModelScope.launch {
            reviewRepository.deleteReview(movieId, mediaType)
                .onSuccess { loadReviews() }
                .onFailure { e ->
                    _uiState.update { it.copy(errorMessage = e.message) }
                }
        }
    }

    fun loadReviews() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            reviewRepository.getReviews()
                .onSuccess { reviews ->
                    _uiState.update { it.copy(isLoading = false, reviews = reviews) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
        }
    }
}
