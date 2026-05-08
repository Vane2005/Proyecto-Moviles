package com.example.cinelog.data.repository

import com.example.cinelog.data.model.CreditsResponse
import com.example.cinelog.data.model.Movie
import com.example.cinelog.data.model.MovieDetail
import com.example.cinelog.data.model.TvDetail
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

    suspend fun getMovieDetail(id: Int): Result<MovieDetail> {
        return try {
            Result.success(api.getMovieDetail(id, appendToResponse = "videos"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMovieCredits(id: Int): Result<CreditsResponse> {
        return try {
            Result.success(api.getMovieCredits(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTvDetail(id: Int): Result<TvDetail> {
        return try {
            Result.success(api.getTvDetail(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
