package com.joshrose.routes

import com.joshrose.Constants.USER_ALREADY_BLOCKED
import com.joshrose.Constants.USER_NOT_BLOCKED
import com.joshrose.plugins.dao
import com.joshrose.util.toUsername
import com.joshrose.validations.validateUsernameExists
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock.System

fun Route.blockRoute() {
    route("/block") {
        install(RequestValidation) {
            validateUsernameExists()
        }

        authenticate {
            get {
                val user = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString().toUsername()
                val blockList = dao.user(user)!!.blockedList
                call.respond(OK, blockList)
            }

            post {
                val (otherUsername, username) = receiveIds()
                if (otherUsername == null) return@post

                val user = dao.user(username)!!
                val blockedList = user.blockedList
                if (blockedList.contains(otherUsername)) call.respond(Conflict, USER_ALREADY_BLOCKED)
                else {
                    dao.editUser(
                        user = user.copy(
                            lastOnline = System.now(),
                            blockedList = blockedList.plus(otherUsername)
                        )
                    )
                    call.respond(OK, "${dao.user(otherUsername)!!.username.name} is blocked!")
                }
            }

            post("/unblock") {
                val (otherUsername, username) = receiveIds()
                if (otherUsername == null) return@post

                val user = dao.user(username)!!
                val blockedList = user.blockedList
                if (!blockedList.contains(otherUsername)) call.respond(BadRequest, USER_NOT_BLOCKED)
                else {
                    dao.editUser(
                        user = user.copy(
                            lastOnline = System.now(),
                            blockedList = blockedList.minus(otherUsername)
                        )
                    )
                    call.respond(OK, "${dao.user(otherUsername)!!.username.name} is unblocked!")
                }
            }
        }
    }
}