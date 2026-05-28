package com.example.cinelog.data.repository

import android.content.Context
import com.example.cinelog.data.local.CinelogDatabase
import com.example.cinelog.data.local.dao.UserListDao
import com.example.cinelog.data.local.entity.MovieEntity
import com.example.cinelog.data.model.MovieItem
import com.example.cinelog.domain.model.ListType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalListRepository(private val context: Context) {

    private val database: CinelogDatabase by lazy {
        CinelogDatabase.getDatabase(context)
    }
    private val dao: UserListDao by lazy {
        database.userListDao()
    }

    // Obtener películas de una lista como Flow
    fun getMoviesFromList(listType: ListType): Flow<List<MovieItem>> {
        return dao.getMoviesByListType(listType.name).map { entities ->
            entities.map { entity ->
                MovieItem(
                    movieId = entity.movieId,
                    titulo = entity.titulo,
                    posterPath = entity.posterPath,
                    mediaType = entity.mediaType
                )
            }
        }
    }

    // Insertar una película en una lista
    suspend fun addMovieToList(movie: MovieItem, listType: ListType) {
        val entity = MovieEntity(
            movieId = movie.movieId,
            titulo = movie.titulo,
            posterPath = movie.posterPath,
            mediaType = movie.mediaType,
            listType = listType.name
        )
        dao.insertMovie(entity)
    }

    // Eliminar una película de una lista
    suspend fun removeMovieFromList(movie: MovieItem, listType: ListType) {
        val entity = MovieEntity(
            movieId = movie.movieId,
            titulo = movie.titulo,
            posterPath = movie.posterPath,
            mediaType = movie.mediaType,
            listType = listType.name
        )
        dao.deleteMovie(entity)
    }

    // Eliminar una película por ID
    suspend fun removeMovieByMovieId(movieId: Int, listType: ListType) {
        dao.deleteMovieByMovieIdAndListType(movieId, listType.name)
    }

    // Guardar una lista completa
    suspend fun saveMoviesList(movies: List<MovieItem>, listType: ListType) {
        val entities = movies.map { movie ->
            MovieEntity(
                movieId = movie.movieId,
                titulo = movie.titulo,
                posterPath = movie.posterPath,
                mediaType = movie.mediaType,
                listType = listType.name
            )
        }
        dao.insertMovies(entities)
    }

    // Limpiar una lista
    suspend fun clearList(listType: ListType) {
        dao.deleteAllMoviesFromList(listType.name)
    }

    // Verificar si una película está en una lista
    suspend fun isMovieInList(movieId: Int, listType: ListType): Boolean {
        return dao.isMovieInList(movieId, listType.name)
    }
}
