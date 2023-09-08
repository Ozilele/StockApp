package com.mycoding.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun Application.configureSerialization() {
    install(ContentNegotiation) { // Content Negotiation pozwala na automatyczną konwersję odpowiedzi serwera na        różny format
        // np. na format json lub na format wyspecifikowany przez clienta w nagłówku headers content type
        json()
    }
}
