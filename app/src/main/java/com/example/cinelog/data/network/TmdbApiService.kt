package com.example.cinelog.data.network

import com.example.cinelog.data.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApiService {

    @GET("trending/movie/week")
    suspend fun getTrendingMovies(): MovieResponse

    @GET("discover/movie")
    suspend fun getMoviesByGenre(
        @Query("with_genres") genreID: Int,
        @Query("sort_by") sortBy: String = "popularity.desc"
    ): MovieResponse
}
