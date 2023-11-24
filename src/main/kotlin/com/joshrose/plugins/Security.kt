package com.joshrose.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import kotlinx.serialization.Serializable

fun Application.configureSecurity() {

    authentication {
        basic {
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
