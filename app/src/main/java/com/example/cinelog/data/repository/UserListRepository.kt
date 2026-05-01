package com.example.cinelog.data.repository

import com.example.cinelog.data.model.MovieItem
import com.example.cinelog.domain.model.ListType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
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

    suspend fun addMovieToList(movie: MovieItem, listType: ListType): Result<Boolean> {
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
        TODO("Por implementar")
    }

    suspend fun getMoviesFromList(listType: ListType): Result<List<MovieItem>> {
        TODO("Por implementar")
    }
}