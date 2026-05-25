package com.example.cinelog.ui.home

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinelog.data.network.NetworkError
import com.example.cinelog.data.network.NetworkHelper
import com.example.cinelog.data.network.classifyError
import com.example.cinelog.data.repository.MovieRepository
import com.example.cinelog.domain.model.HomeState
import com.example.cinelog.domain.model.HomeContentTab
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val movieRepository: MovieRepository = MovieRepository(),
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    init {
        loadHomeContent()
    }

    fun selectTab(tab: HomeContentTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun retryLoad() {
        viewModelScope.launch {
            // Primero verifica si hay conexión
            if (NetworkHelper.isOnline(context)) {
                // Si hay conexión, resetea el estado y carga
                _uiState.update { it.copy(isOffline = false, errorMessage = null) }
                loadHomeContent()
            } else {
                // Si no hay conexión, mantiene el estado offline
                _uiState.update { 
                    it.copy(
                        isOffline = true,
                        errorMessage = "Sin conexión a internet. Verifica tu conexión y intenta de nuevo."
                    ) 
                }
            }
        }
    }  

    private fun loadHomeContent() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, errorType = null) }

            // Verificar conexión a internet
            if (!NetworkHelper.isOnline(context)) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isOffline = true,
                        errorMessage = "Sin conexión a internet. Verifica tu conexión y intenta de nuevo.",
                        errorType = NetworkError.NoConnection
                    ) 
                }
                return@launch
            }

            try {
                // Lanzar todas las peticiones en paralelo
                val trendingDeferred = async { movieRepository.getTrendingMovies() }
                val horrorDeferred = async { movieRepository.getMoviesByGenre(27) }
                val romanceDeferred = async { movieRepository.getMoviesByGenre(10749) }
                val animationDeferred = async { movieRepository.getMoviesByGenre(16) }
                // Series
                val trendingTvDeferred = async { movieRepository.getTrendingTv() }
                val dramaTvDeferred = async { movieRepository.getTvByGenre(18) }
                val romanceTvDeferred = async { movieRepository.getTvByGenre(10749) }
                val animationTvDeferred = async { movieRepository.getTvByGenre(16) }

                // Movies
                val trendingResult = trendingDeferred.await()
                val horrorResult = horrorDeferred.await()
                val romanceResult = romanceDeferred.await()
                val animationResult = animationDeferred.await()
                // Series
                val trendingTvResult = trendingTvDeferred.await()
                val dramaTvResult = dramaTvDeferred.await()
                val romanceTvResult = romanceTvDeferred.await()
                val animationTvResult = animationTvDeferred.await()

                // Verificar si hubo algún error
                val allResults = listOf(horrorResult, romanceResult, animationResult, dramaTvResult, romanceTvResult, animationTvResult)
                val firstError = allResults
                    .firstOrNull { it.isFailure }
                    ?.exceptionOrNull()
                    ?.let { if (it is Exception) classifyError(it) else NetworkError.Unknown }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        featuredMovie = trendingResult.getOrNull()?.firstOrNull(),
                        horrorMovies = horrorResult.getOrDefault(emptyList()),
                        romanceMovies = romanceResult.getOrDefault(emptyList()),
                        animationMovies = animationResult.getOrDefault(emptyList()),

                        featuredSeries = trendingTvResult.getOrNull()?.firstOrNull(),
                        dramaSeries = dramaTvResult.getOrDefault(emptyList()),
                        romanceSeries = romanceTvResult.getOrDefault(emptyList()),
                        animationSeries = animationTvResult.getOrDefault(emptyList()),

                        errorType = firstError,
                        errorMessage = firstError?.let { error ->
                            when (error) {
                                NetworkError.NoConnection -> "No hay conexión a internet"
                                NetworkError.Timeout -> "La conexión tardó mucho. Intenta de nuevo."
                                NetworkError.ServerError -> "Error del servidor. Intenta más tarde."
                                NetworkError.Unknown -> "Error de conexión. Intenta de nuevo."
                            }
                        }
                    )
                }
            } catch (e: Exception) {
                val error = if (e is Exception) classifyError(e) else NetworkError.Unknown
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Ocurrió un error inesperado. Intenta de nuevo.",
                        errorType = error
                    )
                }
            }
        }
    }
}
