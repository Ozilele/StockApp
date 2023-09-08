package com.mycoding.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class AuthRegister(
  val name: String,
  val email: String,
  val password: String,
)

@Serializable
data class AuthLogin(
  val email: String,
  val password: String
)
