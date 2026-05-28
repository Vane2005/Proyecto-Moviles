package com.example.cinelog.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val movieId: Int,
    val titulo: String,
    val posterPath: String,
    val mediaType: String,
    val listType: String // "watchlist", "favoritas", "yaVisto"
)
