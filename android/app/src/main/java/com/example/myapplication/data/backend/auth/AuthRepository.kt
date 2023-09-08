package com.example.myapplication.data.backend.auth

interface AuthRepository {
  suspend fun signUp(name: String, email: String, password: String): AuthResult<Unit>
  suspend fun signIn(email: String, password: String): AuthResult<Unit>
  suspend fun authenticate(): AuthResult<Unit>
}