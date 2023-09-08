package com.mycoding.plugins

import com.mycoding.data.user.PostgresUserDataSource
import com.mycoding.routes.*
import com.mycoding.security.hashing.HashingService
import com.mycoding.security.token.TokenConfig
import com.mycoding.security.token.TokenService
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import org.ktorm.database.Database

fun Application.configureRouting(database: Database, hashingService: HashingService, tokenService: TokenService, tokenConfig: TokenConfig) {
    routing {
        route("/auth") {
            val userDataSource = PostgresUserDataSource(database)
            registerUser(userDataSource, hashingService)
            loginUser(userDataSource, hashingService, tokenService, tokenConfig)
            authenticate()
            getSecretInfo()
        }

        static {
            resources("static")
        }
    }
}
