package com.joshrose.routes

import com.joshrose.plugins.dao
import com.joshrose.requests.StatusRequest
import com.joshrose.validations.validateStatus
import io.ktor.http.HttpStatusCode.Companion.Accepted
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

fun Route.statusRoute() {
    route("/status") {
        install(RequestValidation) {
            validateStatus()
        }

        authenticate {
            get {
                val user = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString()
                val status = dao.user(user)!!.status
                call.respond(OK, status ?: "")
            }

            post {
                val request = try {
                    call.receive<StatusRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@post
                }

                // TODO: Remove ID

                val username = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString()
                val user = dao.user(username)!!
                dao.editUser(
                    user.copy(
                        lastOnline = LocalDateTime.now(),
                        status = request.status
                    )
                )
                call.respond(Accepted, "Status updated!")
            }
        }
    }
}