package com.joshrose.routes

import com.joshrose.plugins.dao
import com.joshrose.requests.StatusRequest
import com.joshrose.util.receiveOrNull
import com.joshrose.util.toUsername
import com.joshrose.validations.validateStatus
import io.ktor.http.HttpStatusCode.Companion.Accepted
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock

fun Route.statusRoute() {
    route("/status") {
        install(RequestValidation) {
            validateStatus()
        }

        authenticate {
            get {
                val user = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString().toUsername()
                val status = dao.user(user)!!.status
                call.respondNullable(OK, status)
            }

            post {
                val request = call.receiveOrNull<StatusRequest>() ?: run {
                    call.respond(BadRequest)
                    return@post
                }

                val username = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString().toUsername()
                val user = dao.user(username)!!
                dao.editUser(
                    user.copy(
                        lastOnline = Clock.System.now(),
                        status = request.status
                    )
                )
                call.respond(Accepted, "Status updated!")
            }
        }
    }
}