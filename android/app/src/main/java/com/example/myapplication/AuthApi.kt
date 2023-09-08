package com.example.myapplication

import com.example.myapplication.data.backend.data.requests.AuthLoginRequest
import com.example.myapplication.data.backend.data.requests.AuthRegisterRequest
import com.example.myapplication.data.backend.data.responses.LoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {

  companion object {
    const val BASE_URL = "http://192.168.1.4:5500"
  }

  @POST("/auth/register")
  suspend fun registerUser(@Body userData: AuthRegisterRequest)

  @POST("/auth/login")
  suspend fun loginUser(@Body userLoginData: AuthLoginRequest): LoginResponse

  @GET("/auth/authenticate")
  suspend fun authenticate(
    @Header("Authorization") token: String
  )

}