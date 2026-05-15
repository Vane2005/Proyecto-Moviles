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

    fun loadReviewForMovie(movieId: Int, mediaType: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoadingReview = true,
                    errorMessage = null,
                    isSaveSuccessful = false,
                    successToastMessage = null,
                    hasExistingReview = false,
                    calificacion = 0,
                    reseña = "",
                    etiquetas = ""
                )
            }

            reviewRepository.getReview(movieId, mediaType)
                .onSuccess { review ->
                    if (review != null) {
                        _uiState.update {
                            it.copy(
                                calificacion = review.calificacion,
                                reseña = review.reseña,
                                etiquetas = review.etiquetas,
                                hasExistingReview = true,
                                isLoadingReview = false
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoadingReview = false, hasExistingReview = false) }
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoadingReview = false,
                            errorMessage = e.message ?: "No se pudo cargar la reseña"
                        )
                    }
                }
        }
    }

    fun saveReview(movieId: Int, mediaType: String, titulo: String, posterPath: String) {
        val wasEditing = _uiState.value.hasExistingReview

        if (_uiState.value.calificacion == 0) {
            _uiState.update { it.copy(errorMessage = "Selecciona una calificación") }
            return
        }
        if (_uiState.value.reseña.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Escribe una reseña") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val state = _uiState.value
            val review = Review(
                movieId = movieId,
                titulo = titulo,
                posterPath = posterPath,
                calificacion = state.calificacion,
                reseña = state.reseña,
                etiquetas = state.etiquetas,
                mediaType = mediaType
            )

            reviewRepository.saveReview(review)
                .onSuccess {
                    val movieItem = MovieItem(
                        movieId = movieId,
                        titulo = titulo,
                        posterPath = posterPath,
                        mediaType = mediaType
                    )
                    val toast = if (wasEditing) "Reseña actualizada" else "Reseña guardada con éxito"
                    userListRepository.addMovieToList(movieItem, ListType.YA_VISTO)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSaveSuccessful = true,
                            successToastMessage = toast,
                            hasExistingReview = true
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message)
                    }
                }
        }
    }
    
    // UserReviewsViewModel
    fun deleteReview(documentId: String) {
        viewModelScope.launch {
            reviewRepository.deleteReview(documentId)
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
