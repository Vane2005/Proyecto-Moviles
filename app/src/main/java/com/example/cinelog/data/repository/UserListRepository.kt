package com.example.cinelog.data.repository

import com.example.cinelog.data.model.MovieItem
import com.example.cinelog.domain.model.ListType
import com.example.cinelog.domain.model.User

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import kotlinx.coroutines.tasks.await

class UserListRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getListName(listType: ListType): String {
        return when (listType) {
            ListType.WATCHLIST -> "watchlist"
            ListType.FAVORITAS -> "favoritas"
            ListType.YA_VISTO -> "yaVisto"
        }
    }

    private fun getUserDocument() = auth.currentUser?.uid?.let {
        firestore.collection("users").document(it)
    }

    private fun mergeYaVistoEntry(
        existing: MovieItem?,
        incoming: MovieItem,
        defaultTimestamp: String
    ): MovieItem {
        val titulo = incoming.titulo.ifBlank { existing?.titulo ?: "" }
        val posterPath = incoming.posterPath.ifBlank { existing?.posterPath ?: "" }
        return MovieItem(
            movieId = incoming.movieId,
            titulo = titulo,
            posterPath = posterPath,
        )
    }

    suspend fun addOrUpdateYaVisto(movie: MovieItem): Result<Boolean> {
        return try {
            val doc = getUserDocument()
                ?: return Result.failure(Exception("Usuario no autenticado"))
            doc.update("yaVisto", FieldValue.arrayUnion(movie)).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addMovieToList(movie: MovieItem, listType: ListType): Result<Boolean> {
        if (listType == ListType.YA_VISTO) {
            return addOrUpdateYaVisto(movie)
        }
        return try {
            val doc = getUserDocument()
                ?: return Result.failure(Exception("Usuario no autenticado"))
            doc.update(getListName(listType), FieldValue.arrayUnion(movie)).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeMovieFromList(movie: MovieItem, listType: ListType): Result<Boolean> {
        if (listType == ListType.YA_VISTO) {
            return removeYaVistoByMovieId(movie.movieId)
        }
        return try {
            val doc = getUserDocument()
                ?: return Result.failure(Exception("Usuario no autenticado"))

            doc.update(getListName(listType), FieldValue.arrayRemove(movie)).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeYaVistoByMovieId(movieId: Int): Result<Boolean> {
        return try {
            val docRef = getUserDocument()
                ?: return Result.failure(Exception("Usuario no autenticado"))
            if (movieId == 0) {
                return Result.failure(Exception("movieId inválido"))
            }

            val snapshot = docRef.get().await()
            val user = snapshot.toObject(User::class.java)
                ?: return Result.failure(Exception("Usuario no encontrado"))

            val nuevos = user.yaVisto.filterNot { it.movieId == movieId }
            docRef.update("yaVisto", nuevos).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMoviesFromList(listType: ListType): Result<List<MovieItem>> {
        return try {
            val docRef = getUserDocument()
                ?: return Result.failure(Exception("Usuario no autenticado"))

            val snapshot = docRef.get().await()
            val user = snapshot.toObject(User::class.java)
                ?: return Result.failure(Exception("Usuario no encontrado"))

            val list = when (listType) {
                ListType.WATCHLIST -> user.watchlist
                ListType.FAVORITAS -> user.favoritas
                ListType.YA_VISTO -> user.yaVisto
            }

            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}