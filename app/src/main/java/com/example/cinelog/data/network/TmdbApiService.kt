package com.example.cinelog.data.network

import com.example.cinelog.data.model.CreditsResponse
import com.example.cinelog.data.model.MovieDetail
import com.example.cinelog.data.model.MovieResponse
import com.example.cinelog.data.model.TvDetail
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {
    @GET("trending/tv/week")
    suspend fun getTrendingTv(): MovieResponse

    @GET("discover/tv")
    suspend fun getTvByGenre(
        @Query("with_genres") genreID: Int,
        @Query("sort_by") sortBy: String = "popularity.desc"
    ): MovieResponse

    @GET("trending/movie/week")
    suspend fun getTrendingMovies(): MovieResponse

    @GET("discover/movie")
    suspend fun getMoviesByGenre(
        @Query("with_genres") genreID: Int,
        @Query("sort_by") sortBy: String = "popularity.desc"
    ): MovieResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(@Path("movie_id") movieId: Int, @Query("append_to_response") appendToResponse: String?): MovieDetail

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(@Path("movie_id") movieId: Int): CreditsResponse

    @GET("tv/{tv_id}")
    suspend fun getTvDetail(@Path("tv_id") tvId: Int, @Query("append_to_response") appendToResponse: String?): TvDetail

    @GET("search/multi")
    suspend fun searchMulti(
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): MovieResponse

}
