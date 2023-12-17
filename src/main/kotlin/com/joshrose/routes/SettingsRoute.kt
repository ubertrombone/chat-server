package com.joshrose.routes

import com.joshrose.plugins.dao
import com.joshrose.plugins.friendRequestDao
import com.joshrose.requests.UpdatePasswordRequest
import com.joshrose.requests.UpdateUsernameRequest
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
import kotlinx.datetime.Clock

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
                    user = user.copy(password = getHashWithSalt(request.newPassword), lastOnline = Clock.System.now())
                ).also { call.respond(OK, "Password reset successfully!") }
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
                        lastOnline = Clock.System.now()
                    )
                ).also { call.respond(OK, "Username changed: ${request.newUsername}") }
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
                    // Keep username in friends lists to avoid performance hit and
                    // because the user will just appear offline forever
                    // TODO: Archive username, last online, and status
                    friendRequestDao.deleteUserFromRequests(username)
                    dao.deleteUser(username)
                    call.respond(OK, "Account Deleted!")
                } else call.respond(BadRequest, "Request to delete account should not be 'false'.")
            }
        }
    }
}