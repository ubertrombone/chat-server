package com.joshrose.routes

import com.joshrose.plugins.dao
import com.joshrose.requests.UpdatePasswordRequest
import com.joshrose.requests.UpdateUsernameRequest
import com.joshrose.responses.SimpleResponse
import com.joshrose.security.getHashWithSalt
import com.joshrose.util.toUsername
import com.joshrose.util.validateUpdateNewUsername
import com.joshrose.util.validateUpdatePassword
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime

fun Route.settingsRoute() {
    route("/settings") {
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

                val username = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString().toUsername()
                val user = dao.user(username)!!
                validateUpdatePassword(username, request)?.let { call.respond(BadRequest, it) } ?: dao.editUser(
                    user = user.copy(password = getHashWithSalt(request.newPassword), lastOnline = LocalDateTime.now())
                ).also { call.respond(OK, SimpleResponse(true, "Password reset successfully!")) }
            }

            post("/updateuser") {
                val request = try {
                    call.receive<UpdateUsernameRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@post
                }

                val username = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString().toUsername()
                val user = dao.user(username)!!
                validateUpdateNewUsername(request)?.let { call.respond(BadRequest, it) } ?: dao.editUser(
                    user = user.copy(
                        username = request.newUsername,
                        lastOnline = LocalDateTime.now()
                    )
                ).also { call.respond(OK, SimpleResponse(true, "Username changed: ${request.newUsername}")) }
            }

            post("/delete") {
                val request = try {
                    call.receive<Boolean>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@post
                }

                val username = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString().toUsername()
                if (request) {
                    dao.deleteUser(username)
                    call.respond(OK, SimpleResponse(true, "Account Deleted!"))
                } else call.respond(BadRequest, "Request to delete account should not be 'false'.")
            }
        }
    }
}