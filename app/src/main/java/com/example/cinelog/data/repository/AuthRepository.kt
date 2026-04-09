package com.example.cinelog.data.repository

class AuthRepository {
    fun login(username: String, password: String): Result<Boolean> {
        return if (username.isNotEmpty() && password.isNotEmpty()) {
            Result.success(true)
        } else {
            Result.failure(Exception("Credenciales inválidas"))
        }
    }
}