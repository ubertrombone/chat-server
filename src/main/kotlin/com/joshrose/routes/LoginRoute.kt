package com.joshrose.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.joshrose.plugins.dao
import com.joshrose.requests.LoginRequest
import com.joshrose.responses.SimpleResponse
import com.joshrose.validations.validateLogin
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
    route("/login") {
        install(RequestValidation) {
            validateLogin()
        }

        post {
            val user = try {
                call.receive<LoginRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(BadRequest)
                return@post
            }

            val token = JWT.create().apply {
                withIssuer(issuer)
                withClaim("username", user.username)
                withExpiresAt(Date(System.currentTimeMillis() + 600000))
            }.sign(Algorithm.HMAC256(secret))
            call.respond(hashMapOf("token" to token))
        }
    }

    authenticate {
        post("/logout") {
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.payload.getClaim("username").asString()
            val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())
            println("EXPIRES AT: $expiresAt")

            val user = dao.user(username)!!
            dao.editUser(user.copy(isOnline = false, lastOnline = LocalDateTime.now()))
            call.respond(OK, SimpleResponse(true, "You are now logged out!"))
        }
    }
}