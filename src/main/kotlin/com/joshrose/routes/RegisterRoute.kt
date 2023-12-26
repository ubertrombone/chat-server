package com.joshrose.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.joshrose.plugins.dao
import com.joshrose.requests.AccountRequest
import com.joshrose.security.getHashWithSalt
import com.joshrose.validations.validateUsername
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import java.util.*

fun Route.registerRoute(issuer: String, secret: String) {
    route("/register") {
        install(RequestValidation) {
            validateUsername()
        }

        post {
            val request = try {
                call.receive<AccountRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(BadRequest)
                return@post
            }

            dao.addNewUser(
                username = request.username,
                password = getHashWithSalt(request.password),
                isOnline = true,
                lastOnline = Clock.System.now(),
                friendList = emptySet(),
                blockedList = emptySet(),
                status = null,
                cache = false
            )?.let {
                val token = JWT.create().apply {
                    withIssuer(issuer)
                    withClaim("username", request.username.name)
                    withExpiresAt(Date(System.currentTimeMillis() + 600000))
                }.sign(Algorithm.HMAC256(secret))
                call.respond(OK, token)
            } ?: call.respond(BadRequest, "An unknown error occurred")
        }
    }
}