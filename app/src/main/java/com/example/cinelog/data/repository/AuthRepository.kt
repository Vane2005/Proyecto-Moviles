package com.example.cinelog.data.repository

import com.example.cinelog.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.EmailAuthProvider
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

    suspend fun getUserProfile(uid: String): Result<User> {
        return try {
            val document = firestore.collection("users").document(uid).get().await()
            val user = document.toObject(User::class.java)
                ?: return Result.failure(Exception("Usuario no encontrado"))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(nombre: String, edad: Int, email: String): Result<Boolean> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No hay sesión activa"))

            // Actualizar datos en Firestore
            val updates = mapOf(
                "nombre" to nombre,
                "edad" to edad,
                "email" to email
            )
            firestore.collection("users").document(uid).update(updates).await()

            // Actualizar email en Firebase Auth si cambió
            val currentEmail = auth.currentUser?.email
            if (email != currentEmail) {
                auth.currentUser?.updateEmail(email)?.await()
            }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Boolean> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> {

        return try {

            val user = FirebaseAuth.getInstance().currentUser
                ?: return Result.failure(Exception("Usuario no autenticado"))

            val email = user.email
                ?: return Result.failure(Exception("Correo no encontrado"))

            val credential = EmailAuthProvider.getCredential(
                email,
                currentPassword
            )

            user.reauthenticate(credential).await()

            user.updatePassword(newPassword).await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }



}