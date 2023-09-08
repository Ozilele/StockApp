package com.mycoding

import com.mycoding.plugins.*
import com.mycoding.security.hashing.SHA256HashingService
import com.mycoding.security.token.JwtTokenService
import com.mycoding.security.token.TokenConfig
import io.ktor.server.application.*
import org.ktorm.database.Database
import org.ktorm.support.postgresql.PostgreSqlDialect

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    val database = Database.connect(
        url = "jdbc:postgresql://localhost:5432/androidstockapp",
        dialect = PostgreSqlDialect(),
        driver = "org.postgresql.Driver",
        user = "bartiq",
    )

    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 1000L * 24L * 3600L,
        secret = System.getenv("JWT_SECRET")
    )
    val hashingService = SHA256HashingService()

    configureRouting(database, hashingService, tokenService, tokenConfig)
    configureSerialization()
    configureMonitoring()
    configureSecurity(tokenConfig)
}
