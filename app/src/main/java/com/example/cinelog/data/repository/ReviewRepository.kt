package com.example.cinelog.data.repository

import com.example.cinelog.domain.model.Review
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ReviewRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getUserDocument() = auth.currentUser?.uid?.let {
        firestore.collection("users").document(it)
    }

    suspend fun saveReview(review: Review): Result<Boolean> {
        return try {
            val doc = getUserDocument()
                ?: return Result.failure(Exception("Usuario no autenticado"))

            if (review.movieId == 0) {
                return Result.failure(Exception("movieId inválido"))
            }

            if (review.calificacion !in 1..5) {
                return Result.failure(Exception("La calificación debe estar entre 1 y 5"))
            }

            if (review.reseña.isBlank()) {
                return Result.failure(Exception("La reseña no puede estar vacía"))
            }

            doc.collection("reviews")
                .document(review.movieId.toString())
                .set(review)
                .await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}