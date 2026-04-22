package com.example.cinelog.data.repository

import com.example.cinelog.data.model.Movie
import com.example.cinelog.data.network.RetrofitClient

class MovieRepository {

    private val api = RetrofitClient.tmdbApiService

    suspend fun getTrendingMovies(): Result<List<Movie>> {
        return try {
            val response = api.getTrendingMovies()
            Result.success(response.results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMoviesByGenre(genreId: Int): Result<List<Movie>> {
        return try {
            val response = api.getMoviesByGenre(genreId)
            Result.success(response.results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}