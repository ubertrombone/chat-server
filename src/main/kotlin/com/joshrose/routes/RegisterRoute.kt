package com.joshrose.routes

import com.joshrose.plugins.dao
import com.joshrose.requests.AccountRequest
import com.joshrose.responses.SimpleResponse
import com.joshrose.security.getHashWithSalt
import com.joshrose.validations.validatePassword
import com.joshrose.validations.validateUsername
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime

fun Route.registerRoute() {
    route("/register") {
        install(RequestValidation) {
            validateUsername()
            validatePassword()
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
                lastOnline = LocalDateTime.now(),
                friendList = null,
                blockedList = null,
                status = null
            )?.let {
                call.respond(OK, SimpleResponse(true, "Successfully created account!"))
            } ?: call.respond(OK, SimpleResponse(false, "An unknown error occurred"))
        }
    }
}