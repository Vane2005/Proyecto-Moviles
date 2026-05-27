package com.example.cinelog.ui.review



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinelog.data.repository.ReviewRepository
import com.example.cinelog.domain.model.Review
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ViewReviewUserUiState(
    val review: Review? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ViewReviewUserViewModel(
    private val reviewRepository: ReviewRepository = ReviewRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ViewReviewUserUiState())
    val uiState: StateFlow<ViewReviewUserUiState> = _uiState.asStateFlow()

    fun loadReview(movieId: Int, mediaType: String) {

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            reviewRepository.getReview(movieId, mediaType)
                .onSuccess { review ->

                    _uiState.update {
                        it.copy(
                            review = review,
                            isLoading = false
                        )
                    }
                }
                .onFailure { e ->

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message
                        )
                    }
                }
        }
    }
}