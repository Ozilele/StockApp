package com.mycoding.data.user

import com.mycoding.data.model.tabels.Users
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

class PostgresUserDataSource(
    private val database: Database
) : UserDataSource {

    override suspend fun registerUser(user : User) : Boolean {
        val isUserAlreadyRegistered = database.sequenceOf(Users)
            .find { usr ->
                usr.email eq user.email
            }
        if(isUserAlreadyRegistered != null) {
            return false // User is already registered
        }
        val newUser = com.mycoding.data.model.entities.User {
            userName = user.name
            userEmail = user.email
            userPassword = user.password
            userSalt = user.salt
        }
        val affectedRecordsNumber = database.sequenceOf(Users)
            .add(newUser)

        return affectedRecordsNumber == 1
    }

    override suspend fun getUser(email: String) : User? {
        val user = database.sequenceOf(Users)
            .find { user->
                user.email eq email
            } ?: return null
        return User(
            name = user.userName,
            email = user.userEmail,
            password = user.userPassword,
            salt = user.userSalt
        )
    }
}