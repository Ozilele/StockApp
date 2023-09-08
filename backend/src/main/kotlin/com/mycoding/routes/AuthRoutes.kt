package com.mycoding.routes

import com.mycoding.data.requests.AuthLogin
import com.mycoding.data.requests.AuthRegister
import com.mycoding.data.responses.AuthResponse
import com.mycoding.data.user.PostgresUserDataSource
import com.mycoding.data.user.User
import com.mycoding.security.hashing.HashingService
import com.mycoding.security.hashing.SaltedHash
import com.mycoding.security.token.TokenClaim
import com.mycoding.security.token.TokenConfig
import com.mycoding.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.lang.Exception


fun Route.registerUser(
    userDataSource: PostgresUserDataSource,
    hashingService: HashingService
) {
    post("/register") {
        try {
            val request = call.receiveOrNull<AuthRegister>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val areFieldsBlank = request.name.isBlank() || request.password.isBlank()
            val regex = Regex(".*\\d+.*") // hasło zawiera przynajmniej jedna cyfre

            if (areFieldsBlank || !regex.containsMatchIn(request.password)) {
                call.respond(HttpStatusCode.Conflict)
                return@post
            }
            val saltedHash = hashingService.generateSaltedHash(request.password)
            val user = User(
                name = request.name,
                email = request.email,
                password = saltedHash.hash,
                salt = saltedHash.salt
            )
            val result = userDataSource.registerUser(user)
            if (!result) {
                call.respond(HttpStatusCode.Conflict)
                return@post
            }
            call.respond(HttpStatusCode.OK)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, "Błąd przetwarzania")
        }
    }
}

fun Route.loginUser(
    userDataSource: PostgresUserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("/login") {
        val request = call.receiveOrNull<AuthLogin>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val user = userDataSource.getUser(request.email)
        if(user == null) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }
        val isPasswordCorrect = hashingService.verifyPassword(request.password, SaltedHash(
            hash = user.password,
            salt = user.salt
        ))
        if(!isPasswordCorrect) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }
        // User is logged in, generate a token
        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userEmail",
                value = user.email
            ),
            TokenClaim(
                name = "userName",
                value = user.name
            )
        )
        println("Token is $token")

        call.respond(
            status = HttpStatusCode.OK,
            message = AuthResponse(
                token = token
            )
        )
    }
}

fun Route.authenticate() {
    authenticate {
        get("/authenticate") {
            call.respond(HttpStatusCode.OK) // status code 200
        }
    }
}

fun Route.getSecretInfo() {
    authenticate{
        get("/secret") {
            val principal = call.principal<JWTPrincipal>()
            val userEmail = principal?.getClaim("userEmail", String::class)
            call.respond(HttpStatusCode.OK, "Your email is $userEmail")
        }
    }
}