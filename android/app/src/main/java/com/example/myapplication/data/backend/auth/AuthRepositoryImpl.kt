package com.example.myapplication.data.backend.auth

import android.content.SharedPreferences
import com.example.myapplication.AuthApi
import com.example.myapplication.data.backend.data.requests.AuthLoginRequest
import com.example.myapplication.data.backend.data.requests.AuthRegisterRequest
import retrofit2.HttpException
import javax.inject.Named

class AuthRepositoryImpl(
  private val authApi: AuthApi,
  private val prefs: SharedPreferences
) : AuthRepository {

  override suspend fun signUp(name: String, email: String, password: String): AuthResult<Unit> {
    return try {
      authApi.registerUser(AuthRegisterRequest(
        name = name,
        email = email,
        password = password
      ))
      signIn(email, password)
    } catch(e: HttpException) {
      if(e.code() == 401) { // User is in unauthorized state
        AuthResult.Unauthorized()
      } else {
        AuthResult.UnknownError()
      }
    } catch(e: Exception) {
      AuthResult.UnknownError()
    }
  }

  override suspend fun signIn(email: String, password: String): AuthResult<Unit> {
    return try {
      val response = authApi.loginUser(AuthLoginRequest(
        email = email,
        password = password
      ))
      prefs.edit()
        .putString("jwt", response.token)
        .apply()
      AuthResult.Authorized()
    } catch(e: HttpException) {
      if(e.code() == 401) { // User is in unauthorized state
        AuthResult.Unauthorized()
      } else {
        AuthResult.UnknownError()
      }
    } catch(e: Exception) {
      AuthResult.UnknownError()
    }
  }

  override suspend fun authenticate(): AuthResult<Unit> {
    return try {
      val token = prefs.getString("jwt", null) ?: return AuthResult.Unauthorized()
      println("Tokenik z ApiRepositoryImpl: $token")
      authApi.authenticate("Bearer $token")
      AuthResult.Authorized()
    } catch(e: HttpException) {
      if(e.code() == 401) { // User is in unauthorized state
        AuthResult.Unauthorized()
      } else {
        AuthResult.UnknownError()
      }
    } catch(e: Exception) {
      AuthResult.UnknownError()
    }
  }
}