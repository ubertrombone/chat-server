package com.joshrose.routes

import com.joshrose.plugins.Security
import com.joshrose.plugins.dao
import com.joshrose.requests.*
import com.joshrose.validations.*
import io.ktor.http.HttpStatusCode.Companion.Accepted
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime

@Suppress("DuplicatedCode")
fun Route.friendsRoute() {
    route("/friends") {
        install(RequestValidation) {
            validateAddFriend()
            validateRemoveFriend()
        }

        authenticate {
            get {
                val id = call.principal<Security>()!!.id
                val friendList = dao.user(id)!!.friendList
                call.respond(OK, friendList ?: "")
            }

            post("/add") {
                val request = try {
                    call.receive<AddFriendRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@post
                }

                val user = dao.user(request.id)!!
                val friendList = user.friendList?.split(";") ?: listOf()
                dao.editUser(
                    user = user.copy(
                        lastOnline = LocalDateTime.now(),
                        friendList = if (friendList.isEmpty()) request.otherUser
                        else "${user.friendList};${request.otherUser}"
                    )
                )
                call.respond(Accepted, "${request.otherUser} added!")
            }

            post("/remove") {
                val request = try {
                    call.receive<RemoveFriendRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@post
                }

                val user = dao.user(request.id)!!
                val friendList = user.friendList?.split(";")!!
                val newFriendList = friendList.minus(request).joinToString(";")
                dao.editUser(
                    user = user.copy(
                        lastOnline = LocalDateTime.now(),
                        friendList = newFriendList
                    )
                )
                call.respond(Accepted, "${request.otherUser} removed!")
            }
        }
    }
}