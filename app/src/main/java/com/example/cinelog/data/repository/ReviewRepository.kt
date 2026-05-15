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

    private fun getDocId(movieId: Int, mediaType: String) = "${mediaType}_$movieId"

    suspend fun getReview(movieId: Int, mediaType: String): Result<Review?> {
        return try {
            val doc = getUserDocument()
                ?: return Result.failure(Exception("Usuario no autenticado"))

            val snapshot = doc.collection("reviews")
                .document(getDocId(movieId, mediaType))
                .get()
                .await()

            if (!snapshot.exists()) {
                Result.success(null)
            } else {
                Result.success(snapshot.toObject(Review::class.java))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveReview(review: Review): Result<Boolean> {
        return try {
            val doc = getUserDocument()
                ?: return Result.failure(Exception("Usuario no autenticado"))

            if (review.movieId == 0) {
                return Result.failure(Exception("ID inválido"))
            }

            doc.collection("reviews")
                .document(getDocId(review.movieId, review.mediaType))
                .set(review)
                .await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteReview(movieId: Int, mediaType: String): Result<Boolean> {
        return try {
            val doc = getUserDocument()
                ?: return Result.failure(Exception("Usuario no autenticado"))

            doc.collection("reviews")
                .document(getDocId(movieId, mediaType))
                .delete()
                .await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReviews(): Result<List<Review>> {
        return try {
            val doc = getUserDocument()
                ?: return Result.failure(Exception("Usuario no autenticado"))

            val snapshot = doc.collection("reviews").get().await()
            val reviews = snapshot.toObjects(Review::class.java)
            Result.success(reviews)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
