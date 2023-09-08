package com.mycoding.data.model.tabels

import com.mycoding.data.model.entities.User
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object Users: Table<User>("users") { // object is responsible for binding data from database table to our entity, binds tables columns to entities properties
    val id = long("id").primaryKey().bindTo{ it.userID }
    val name = varchar("name").bindTo{ it.userName }
    val email = varchar("email").bindTo { it.userEmail }
    val password = varchar("password").bindTo{ it.userPassword }
    val salt = varchar("salt").bindTo { it.userSalt }
}