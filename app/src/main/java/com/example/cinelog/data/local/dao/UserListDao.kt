package com.example.cinelog.data.local.dao

import androidx.room.*
import androidx.room.Dao
import com.example.cinelog.data.local.entity.MovieEntity
import com.example.cinelog.domain.model.ListType
import kotlinx.coroutines.flow.Flow

@Dao
interface UserListDao {
    // Insertar una película en una lista
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)

    // Insertar varias películas
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    // Eliminar una película de una lista
    @Delete
    suspend fun deleteMovie(movie: MovieEntity)

    // Eliminar una película por ID y tipo de lista
    @Query("DELETE FROM movies WHERE movieId = :movieId AND listType = :listType")
    suspend fun deleteMovieByMovieIdAndListType(movieId: Int, listType: String)

    // Obtener todas las películas de una lista específica
    @Query("SELECT * FROM movies WHERE listType = :listType")
    fun getMoviesByListType(listType: String): Flow<List<MovieEntity>>

    // Obtener todas las películas de una lista (sin Flow)
    @Query("SELECT * FROM movies WHERE listType = :listType")
    suspend fun getMoviesByListTypeSync(listType: String): List<MovieEntity>

    // Verificar si una película está en una lista
    @Query("SELECT EXISTS(SELECT 1 FROM movies WHERE movieId = :movieId AND listType = :listType)")
    suspend fun isMovieInList(movieId: Int, listType: String): Boolean

    // Eliminar todas las películas de una lista
    @Query("DELETE FROM movies WHERE listType = :listType")
    suspend fun deleteAllMoviesFromList(listType: String)

    // Obtener todas las películas de todas las listas
    @Query("SELECT * FROM movies")
    fun getAllMovies(): Flow<List<MovieEntity>>
}
