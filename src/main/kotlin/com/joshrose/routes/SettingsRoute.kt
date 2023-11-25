package com.joshrose.routes

import com.joshrose.plugins.dao
import com.joshrose.requests.RemoveUserRequest
import com.joshrose.requests.UpdatePasswordRequest
import com.joshrose.requests.UpdateUsernameRequest
import com.joshrose.responses.SimpleResponse
import com.joshrose.security.getHashWithSalt
import com.joshrose.validations.validatePasswordUpdate
import com.joshrose.validations.validateUsernameUpdate
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

fun Route.settingsRoute() {
    route("/settings") {
        install(RequestValidation) {
            validateUsernameUpdate()
            validatePasswordUpdate()
        }

        authenticate {
            get {
                val username = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString()
                call.respondText("Hello, $username!")
            }

            post("/updatepwd") {
                val request = try {
                    call.receive<UpdatePasswordRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@post
                }

                // TODO:
                //  1. ValidatePasswordUpdate isn't needed -- change it to a function that returns a reason or null
                //  2. if new fun returns null, editUser else return reason

                val user = dao.user(request.id)!!
                dao.editUser(
                    user = user.copy(
                        password = getHashWithSalt(request.newPassword),
                        lastOnline = LocalDateTime.now()
                    )
                )
                call.respond(OK, SimpleResponse(true, "Password reset successfully!"))
            }

            post("/updateuser") {
                val request = try {
                    call.receive<UpdateUsernameRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@post
                }

                // TODO: Remove ID

                val user = dao.user(request.id)!!
                dao.editUser(
                    user = user.copy(
                        username = request.newUsername,
                        lastOnline = LocalDateTime.now()
                    )
                )
                call.respond(OK, SimpleResponse(true, "Username changed: ${request.newUsername}"))
            }

            post("/delete") {
                val request = try {
                    call.receive<RemoveUserRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@post
                }

                // TODO: Remove ID

                if (request.delete) dao.deleteUser(request.id)
                call.respond(OK, SimpleResponse(true, "Account Deleted!"))
            }
        }
    }
}