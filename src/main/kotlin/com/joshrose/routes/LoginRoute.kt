package com.joshrose.routes

import com.joshrose.plugins.dao
import com.joshrose.requests.LoginRequest
import com.joshrose.requests.LogoutRequest
import com.joshrose.responses.SimpleResponse
import com.joshrose.validations.validateLogin
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime

fun Route.loginRoute() {
    route("/login") {
        install(RequestValidation) {
            validateLogin()
        }

        post {
            try {
                call.receive<LoginRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(BadRequest)
                return@post
            }

            call.respond(OK, SimpleResponse(true, "You are now logged in!"))
        }

        post("/logout") {
            val request = try {
                call.receive<LogoutRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(BadRequest)
                return@post
            }

            val user = dao.user(request.id)!!
            dao.editUser(
                user.copy(isOnline = request.isOnline, lastOnline = LocalDateTime.now())
            )
            call.respond(OK, SimpleResponse(true, "You are now logged out!"))
        }
    }
}