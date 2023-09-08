package com.example.myapplication.data.backend.data.requests

data class AuthRegisterRequest(
  val name: String,
  val email: String,
  val password: String,
)
