package com.example.cinelog.data.repository

import com.example.cinelog.domain.model.ListType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class UserListRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getListCollection(listType: ListType): CollectionReference? {
        val uid = auth.currentUser?.uid ?: return null
        val listName = when (listType) {
            ListType.WATCHLIST -> "watchlist"
            ListType.FAVORITAS -> "favoritas"
            ListType.YA_VISTO -> "yaVisto"
        }
        return firestore.collection("users").document(uid)
            .collection("lists").document(listName)
            .collection("items")
    }

    suspend fun addMovieToList(movie: MovieItem, listType: ListType): Result<Boolean> {
        return try {
            val collection = getListCollection(listType)
                ?: return Result.failure(Exception("Usuario no autenticado"))
            collection.document(movie.movieId.toString()).set(movie).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}