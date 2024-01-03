package com.joshrose.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.joshrose.Constants.INCORRECT_CREDS
import com.joshrose.plugins.dao
import com.joshrose.requests.AuthenticationRequest
import com.joshrose.util.receiveOrNull
import com.joshrose.util.toUsername
import com.joshrose.validations.validateAuth
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.async
import kotlinx.datetime.Clock
import java.util.*

fun Route.loginRoute(issuer: String, secret: String) {
    route("/authenticate") {
        install(RequestValidation) {
            validateAuth()
        }

        post {
            val user = call.receiveOrNull<AuthenticationRequest>() ?: run {
                call.respond(BadRequest, INCORRECT_CREDS)
                return@post
            }

            val token = async {
                JWT.create().apply {
                    withIssuer(issuer)
                    withClaim("username", user.username.name)
                    withExpiresAt(Date(System.currentTimeMillis() + 600000))
                }.sign(Algorithm.HMAC256(secret))
            }
            async {
                val username = dao.user(user.username)!!
                dao.editUser(username.copy(isOnline = true))
            }.await()
            call.respond(OK, token.await())
        }
    }

    authenticate {
        get("/login") {
            val username = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString().toUsername()
            val user = dao.user(username)!!
            dao.editUser(user.copy(isOnline = true))
            call.respond(OK, "Token valid!")
        }
    }

    authenticate {
        get("/logout") {
            val username = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString().toUsername()
            val user = dao.user(username)!!
            dao.editUser(user.copy(isOnline = false, lastOnline = Clock.System.now()))
            call.respond(OK, "You are now logged out!")
        }
    }
}