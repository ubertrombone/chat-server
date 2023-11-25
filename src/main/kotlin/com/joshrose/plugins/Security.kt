package com.joshrose.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable

fun Application.configureSecurity() {
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtDomain = environment.config.property("jwt.issuer").getString()
    val jwtRealm = environment.config.property("jwt.realm").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()
    authentication {
        jwt {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                with(credential.payload) {
                    if (getClaim("email").asString() != "")
                        JWTPrincipal(credential.payload)
                    else null
                }
            }
            challenge { _, _ ->
                call.respond(Unauthorized, "Token is not valid or has expired")
            }
        }
    }

    authentication {
        basic("old") {
            realm = "Chat Server"
            validate { credentials ->
                val email = credentials.name.lowercase()
                val password = credentials.password
                if (dao.checkPassword(email, password)) Security(dao.loginUser(email), email) else null
            }
        }
    }
}

@Serializable
data class Security(val id: Int, val email: String) : Principal
