package com.joshrose.routes

import com.joshrose.plugins.archiveDao
import com.joshrose.plugins.dao
import com.joshrose.plugins.friendRequestDao
import com.joshrose.requests.UpdatePasswordRequest
import com.joshrose.requests.UpdateUsernameRequest
import com.joshrose.security.getHashWithSalt
import com.joshrose.util.receiveOrNull
import com.joshrose.util.toUsername
import com.joshrose.util.validateUpdateNewUsername
import com.joshrose.util.validateUpdatePassword
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.datetime.Clock

fun Route.settingsRoute() {
    route("/settings") {
        authenticate {
            get {
                val username = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString()
                call.respondText("Hello, $username!")
            }

            post("/updatepwd") {

                val request = call.receiveOrNull<UpdatePasswordRequest>() ?: run {
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
                val request = call.receiveOrNull<UpdateUsernameRequest>() ?: run {
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

            get("/cache") {
                val username = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString().toUsername()
                val cache = dao.user(username)!!.cache
                call.respond(OK, cache)
            }

            post("/cache") {
                val cache = call.receiveOrNull<Boolean>() ?: run {
                    call.respond(BadRequest)
                    return@post
                }

                val username = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString().toUsername()
                val user = dao.user(username)!!
                dao.editUser(
                    user = user.copy(
                        cache = cache,
                        lastOnline = Clock.System.now()
                    )
                )
            }

            post("/delete") {
                val delete = call.receiveOrNull<Boolean>() ?: run {
                    call.respond(BadRequest)
                    return@post
                }

                val username = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString().toUsername()
                if (delete) {
                    // Keep username in friends lists to avoid performance hit and
                    // because the user will just appear offline forever
                    val user = dao.user(username)!!
                    val archiveUser = async { archiveDao.addToArchives(user) }
                    val deleteRequest = async { friendRequestDao.deleteUserFromRequests(username) }
                    val deleteUser = async { dao.deleteUser(username) }
                    awaitAll(archiveUser, deleteRequest, deleteUser)
                    call.respond(OK, "Account Deleted!")
                } else call.respond(BadRequest, "Request to delete account should not be 'false'.")
            }
        }
    }
}