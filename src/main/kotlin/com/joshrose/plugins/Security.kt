package com.joshrose.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun Application.configureSecurity() {
    val jwtDomain = environment.config.property("jwt.issuer").getString()
    val jwtRealm = environment.config.property("jwt.realm").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()
    authentication {
        jwt {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                with(credential.payload) {
                    if (getClaim("username").asString() != "") JWTPrincipal(this) else null
                }
            }
            challenge { _, _ ->
                call.respond(Unauthorized, "Token is not valid or has expired")
            }
        }
    }
}
