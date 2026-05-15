package com.example.cinelog.data.model

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("overview") val overview: String?, // Ahora es opcional
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("vote_average") val voteAverage: Double?, // Opcional
    @SerializedName("genre_ids") val genreIds: List<Int>?,    // Opcional
    @SerializedName("media_type") val mediaType: String?
) {
    val displayTitle: String
        get() = title ?: name ?: "Sin título"

    val displayDate: String
        get() = releaseDate ?: firstAirDate ?: ""
        
    val safeOverview: String
        get() = overview ?: "Sin descripción disponible."
}
