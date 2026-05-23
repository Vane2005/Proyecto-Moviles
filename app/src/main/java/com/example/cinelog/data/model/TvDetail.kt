package com.example.cinelog.data.model

import com.google.gson.annotations.SerializedName

data class TvDetail(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("genres") val genres: List<Genre>,
    @SerializedName("videos") val videos: VideoResponse?,
    @SerializedName("number_of_seasons") val numberOfSeasons: Int
)