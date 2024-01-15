package com.joshrose.routes

import com.joshrose.Constants.FRIEND_ALREADY_ADDED
import com.joshrose.Constants.FRIEND_DOESNT_EXIST
import com.joshrose.Constants.UNKNOWN_ERROR
import com.joshrose.plugins.dao
import com.joshrose.plugins.friendRequestDao
import com.joshrose.util.addFriend
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

fun Route.friendsRoute() {
    route("/friends") {
        install(RequestValidation) {
            validateUsernameExists()
        }

        authenticate {
            get {
                val user = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString().toUsername()
                val blockList = async { dao.user(user)!!.blockedList }
                val friendList = async { dao.getFriends(user) }
                val bAwait = blockList.await()
                val fAwait = friendList.await().filterNot { bAwait.contains(dao.userID(it.username)) }.toSet()
                call.respond(OK, fAwait)
            }

            post("/add") {
                val (otherUser, username) = receiveIds()
                if (otherUser == null) return@post

                val user = dao.user(username)!!
                val friendList = user.friendList

                when {
                    friendList.contains(otherUser) -> call.respond(Conflict, FRIEND_ALREADY_ADDED)
                    friendRequestDao.friendRequestExists(otherUser, username) -> call.addFriend(
                        requesterUsername = dao.user(otherUser)!!.username,
                        userUsername = dao.user(username)!!.username,
                        context = coroutineContext
                    )
                    else -> call.respond(BadRequest, UNKNOWN_ERROR)
                }
            }

            post("/remove") {
                val (otherUser, username) = receiveIds()
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
                    call.respond(OK, "${dao.user(otherUser)!!.username.name} removed!")
                }
            }
        }
    }
}