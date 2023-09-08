package com.mycoding.data.model.entities

import org.ktorm.entity.Entity

interface User : Entity<User> {
    companion object : Entity.Factory<User>()
    var userID: Long
    var userName: String
    var userEmail: String
    var userPassword: String
    var userSalt: String
}