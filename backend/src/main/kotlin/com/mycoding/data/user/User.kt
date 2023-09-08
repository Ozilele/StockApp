package com.mycoding.data.user

data class User(
  val name: String,
  val email: String,
  val password: String,
  val salt: String,
)
