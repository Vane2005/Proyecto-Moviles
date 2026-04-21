package com.example.cinelog.data.repository

import com.example.cinelog.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun login(email: String, password: String): Result<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(nombre: String, edad: Int, email: String, password: String): Result<Boolean> {
        return try {

            // Crear usuario en Authentication
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: return Result.failure(Exception("Error al obtener el UID"))

            // Guardar datos en Firestore
            val user = User(uid = uid, nombre = nombre, edad = edad, email = email)
            firestore.collection("users").document(uid).set(user).await()

            Result.success(true)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}