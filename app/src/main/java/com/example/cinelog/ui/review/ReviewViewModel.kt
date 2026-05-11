package com.example.cinelog.ui.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinelog.data.repository.ReviewRepository
import com.example.cinelog.domain.model.Review
import com.example.cinelog.domain.model.ReviewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val reviewRepository: ReviewRepository = ReviewRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewState())
    val uiState: StateFlow<ReviewState> = _uiState.asStateFlow()

    fun onCalificacionChange(value: Int) {
        _uiState.update { it.copy(calificacion = value, errorMessage = null) }
    }

    fun onReseñaChange(value: String) {
        _uiState.update { it.copy(reseña = value, errorMessage = null) }
    }

    fun saveReview(movieId: Int, titulo: String, posterPath: String) {
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
                reseña = state.reseña
            )

            reviewRepository.saveReview(review)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isSaveSuccessful = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
        }
    }
}