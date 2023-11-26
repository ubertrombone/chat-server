package com.joshrose.routes

import com.joshrose.Constants.USER_ALREADY_BLOCKED
import com.joshrose.Constants.USER_NOT_BLOCKED
import com.joshrose.plugins.dao
import com.joshrose.validations.validateUsernameExists
import io.ktor.http.HttpStatusCode.Companion.Accepted
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime

fun Route.blockRoute() {
    route("/block") {
        install(RequestValidation) {
            validateUsernameExists()
        }

        authenticate {
            get {
                val user = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString()
                val blockList = dao.user(user)!!.blockedList
                call.respond(OK, blockList ?: "")
            }

            post {
                val (request, username) = receiverUsernames()
                if (request == null) return@post

                val user = dao.user(username)!!
                val blockedList = user.blockedList?.split(";")
                if (blockedList?.contains(request.name) == true) call.respond(Conflict, USER_ALREADY_BLOCKED)
                else {
                    dao.editUser(
                        user = user.copy(
                            lastOnline = LocalDateTime.now(),
                            friendList = blockedList?.let { "$it;${request.name}" } ?: request.name
                        )
                    )
                    call.respond(Accepted, "${request.name} is blocked!")
                }
            }

            post("/unblock") {
                val (request, username) = receiverUsernames()
                if (request == null) return@post

                val user = dao.user(username)!!
                val blockedList = user.blockedList?.split(";")
                when {
                    blockedList == null -> call.respond(BadRequest, USER_NOT_BLOCKED)
                    !blockedList.contains(request.name) -> call.respond(BadRequest, USER_NOT_BLOCKED)
                    else -> {
                        dao.editUser(
                            user = user.copy(
                                lastOnline = LocalDateTime.now(),
                                friendList = blockedList.minus(request.name).joinToString(";")
                            )
                        )
                        call.respond(Accepted, "${request.name} is unblocked!")
                    }
                }
            }
        }
    }
}