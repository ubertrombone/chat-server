package com.joshrose.routes

import com.joshrose.Constants.FRIEND_ALREADY_ADDED
import com.joshrose.Constants.FRIEND_DOESNT_EXIST
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
import kotlinx.coroutines.async
import kotlinx.datetime.Clock

@Suppress("DuplicatedCode")
fun Route.friendsRoute() {
    route("/friends") {
        install(RequestValidation) {
            validateUsernameExists()
        }

        authenticate {
            get {
                val user = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString().toUsername()
                val blockList = async { dao.user(user)!!.blockedList }
                val friendList = async { dao.friends(user) }
                val bAwait = blockList.await()
                val fAwait = friendList.await().filterNot { bAwait.contains(it.username) }.toSet()
                call.respond(OK, fAwait)
            }

            post("/add") {
                val (otherUser, username) = receiveUsernames()
                if (otherUser == null) return@post

                val user = dao.user(username)!!
                val friendList = user.friendList
                if (friendList.contains(otherUser)) call.respond(Conflict, FRIEND_ALREADY_ADDED)
                else {
                    dao.editUser(
                        user = user.copy(
                            lastOnline = Clock.System.now(),
                            friendList = friendList.plus(otherUser)
                        )
                    )
                    call.respond(OK, "${otherUser.name} added!")
                }
            }

            post("/remove") {
                val (otherUser, username) = receiveUsernames()
                if (otherUser == null) return@post

                val user = dao.user(username)!!
                val friendList = user.friendList
                if (!friendList.contains(otherUser)) call.respond(BadRequest, FRIEND_DOESNT_EXIST)
                else {
                    dao.editUser(
                        user = user.copy(
                            lastOnline = Clock.System.now(),
                            friendList = friendList.minus(otherUser)
                        )
                    )
                    call.respond(OK, "${otherUser.name} removed!")
                }
            }
        }
    }
}