package com.joshrose.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.joshrose.plugins.dao
import com.joshrose.requests.AuthenticationRequest
import com.joshrose.util.toUsername
import com.joshrose.validations.validateAuth
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime
import java.util.*

fun Route.loginRoute(issuer: String, secret: String) {
    route("/authenticate") {
        install(RequestValidation) {
            validateAuth()
        }

        post {
            val user = try {
                call.receive<AuthenticationRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(BadRequest)
                return@post
            }

            val token = JWT.create().apply {
                withIssuer(issuer)
                withClaim("username", user.username.name)
                withExpiresAt(Date(System.currentTimeMillis() + 600000))
            }.sign(Algorithm.HMAC256(secret))
            call.respond(OK, token)
        }
    }

    authenticate {
        get("/login") {
            call.respond(OK, "Token valid!")
        }
    }

    authenticate {
        get("/logout") {
            val username = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString().toUsername()
            val user = dao.user(username)!!
            dao.editUser(user.copy(isOnline = false, lastOnline = LocalDateTime.now()))
            call.respond(OK, "You are now logged out!")
        }
    }
}