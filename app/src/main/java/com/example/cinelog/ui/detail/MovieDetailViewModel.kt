package com.example.cinelog.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinelog.data.model.MovieDetail
import com.example.cinelog.data.model.MovieItem
import com.example.cinelog.data.model.TvDetail
import com.example.cinelog.data.repository.MovieRepository
import com.example.cinelog.data.repository.UserListRepository
import com.example.cinelog.domain.model.ListType
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MovieDetailUiState(
    val movie: MovieDetail? = null,
    val tvShow: TvDetail? = null,
    val mediaType: String = "movie",
    val director: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showAddOptions: Boolean = false,
    val successMessage: String? = null,
    val inWatchlist: Boolean = false,
    val inFavoritas: Boolean = false,
    val inYaVisto: Boolean = false
) {
    val displayTitle: String
        get() = movie?.title ?: tvShow?.name ?: ""

    val displayPoster: String?
        get() = movie?.posterPath ?: tvShow?.posterPath

    val displayBackdrop: String?
        get() = movie?.backdropPath ?: tvShow?.backdropPath

    val displayOverview: String
        get() = movie?.overview ?: tvShow?.overview ?: ""

    val displayDate: String
        get() = movie?.releaseDate ?: tvShow?.firstAirDate ?: ""

    val displayVoteAverage: Double
        get() = movie?.voteAverage ?: tvShow?.voteAverage ?: 0.0

    val displayId: Int
        get() = movie?.id ?: tvShow?.id ?: 0
}

class MovieDetailViewModel(
    private val movieRepository: MovieRepository = MovieRepository(),
    private val userListRepository: UserListRepository = UserListRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieDetailUiState())
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    fun loadDetail(movieId: Int, mediaType: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(
                isLoading = true, 
                errorMessage = null, 
                movie = null, 
                tvShow = null, 
                director = null,
                mediaType = mediaType
            ) }
            
            if (mediaType == "movie") {
                val detailDeferred = async { movieRepository.getMovieDetail(movieId) }
                val creditsDeferred = async { movieRepository.getMovieCredits(movieId) }

                val detailResult = detailDeferred.await()
                val creditsResult = creditsDeferred.await()

                detailResult.onSuccess { detail ->
                    val crew = creditsResult.getOrNull()?.crew ?: emptyList()
                    val directorMember = crew.find { it.job.trim().equals("Director", ignoreCase = true) }
                    
                    _uiState.update { it.copy(
                        isLoading = false, 
                        movie = detail,
                        director = directorMember?.name ?: "Desconocido"
                    ) }

                    checkIfInLists(detail.id)
                }.onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            } else {
                // TV Show
                val detailDeferred = async { movieRepository.getTvDetail(movieId) }
                val detailResult = detailDeferred.await()

                detailResult.onSuccess { detail ->
                    _uiState.update { it.copy(
                        isLoading = false, 
                        tvShow = detail,
                        director = "Serie de TV" // No director in simple TV detail call
                    ) }
                    checkIfInLists(detail.id)
                }.onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            }
        }
    }

    // Keep original for compatibility if needed elsewhere
    fun loadMovieDetail(movieId: Int) = loadDetail(movieId, "movie")

    fun toggleAddOptions() {
        _uiState.update { it.copy(showAddOptions = !it.showAddOptions) }
    }

    fun dismissAddOptions() {
        _uiState.update { it.copy(showAddOptions = false) }
    }

    private fun checkIfInLists(movieId: Int) {
        viewModelScope.launch {
            val watchlist = userListRepository.getMoviesFromList(ListType.WATCHLIST).getOrDefault(emptyList())
            val favoritas = userListRepository.getMoviesFromList(ListType.FAVORITAS).getOrDefault(emptyList())
            val yaVisto = userListRepository.getMoviesFromList(ListType.YA_VISTO).getOrDefault(emptyList())

            _uiState.update {
                it.copy(
                    inWatchlist = watchlist.any { m -> m.movieId == movieId },
                    inFavoritas = favoritas.any { m -> m.movieId == movieId },
                    inYaVisto = yaVisto.any { m -> m.movieId == movieId }
                )
            }
        }
    }

    fun addToList(listType: ListType) {
        val state = _uiState.value
        val movieId = state.displayId
        if (movieId == 0) return

        val movieItem = MovieItem(
            movieId = movieId,
            titulo = state.displayTitle,
            posterPath = state.displayPoster ?: "",
            mediaType = state.mediaType
        )
        viewModelScope.launch {
            userListRepository.addMovieToList(movieItem, listType)
                .onSuccess {
                    if (listType == ListType.FAVORITAS) {
                        userListRepository.addMovieToList(
                            movieItem,
                            ListType.YA_VISTO
                        )
                    }
                    val mensaje = when (listType) {
                        ListType.WATCHLIST -> "Agregada a Ver más tarde"
                        ListType.FAVORITAS -> "Agregada a Favoritas"
                        ListType.YA_VISTO -> "Agregada a Ya visto"
                    }
                    _uiState.update { it.copy(successMessage = mensaje) }
                    checkIfInLists(movieId)
                    dismissAddOptions()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(errorMessage = e.message) }
                }
        }
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }
}
