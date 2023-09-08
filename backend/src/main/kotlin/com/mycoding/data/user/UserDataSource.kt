package com.mycoding.data.user

interface UserDataSource {
    suspend fun registerUser(user : User) : Boolean
    suspend fun getUser(email: String) : User?
}