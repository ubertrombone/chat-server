package com.joshrose.routes

import com.joshrose.Constants.FRIEND_ALREADY_ADDED
import com.joshrose.Constants.FRIEND_REQUEST_DOESNT_EXIST
import com.joshrose.Constants.FRIEND_REQUEST_EXISTS
import com.joshrose.Constants.REQUEST_ALREADY_RECEIVED
import com.joshrose.plugins.dao
import com.joshrose.plugins.friendRequestDao
import com.joshrose.util.toUsername
import com.joshrose.validations.validateUsernameExists
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.UnprocessableEntity
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.friendRequestRoute() {
    route("/friend_request") {
        install(RequestValidation) {
            validateUsernameExists()
        }

        authenticate {
            get("/sent_friend_requests") {
                val user = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString().toUsername()
                val sentRequests = friendRequestDao.sentFriendRequests(user)
                call.respond(OK, sentRequests)
            }

            get("/received_friend_requests") {
                val user = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString().toUsername()
                val receivedRequests = friendRequestDao.receivedFriendRequests(user)
                call.respond(OK, receivedRequests)
            }

            post {
                val (request, username) = receiveUsernames()
                if (request == null) return@post

                val friendList = dao.user(username)!!.friendList

                when {
                    friendRequestDao.friendRequestExists(username, request) ->
                        call.respond(Conflict, FRIEND_REQUEST_EXISTS)
                    friendRequestDao.friendRequestExists(request, username) ->
                        call.respond(UnprocessableEntity, REQUEST_ALREADY_RECEIVED)
                    else -> {
                        if (friendList.contains(request)) call.respond(Conflict, FRIEND_ALREADY_ADDED)
                        else {
                            friendRequestDao.addNewFriendRequest(requesterUsername = username, toUsername = request)
                            call.respond(OK, "Request Sent!")
                        }
                    }
                }
            }

            post("/cancel_request") {
                val (request, username) = receiveUsernames()
                if (request == null) return@post

                if (friendRequestDao.removeFriendRequest(username, request))
                    call.respond(OK, "Request cancelled!")
                else call.respond(BadRequest, FRIEND_REQUEST_DOESNT_EXIST)
            }
        }
    }
}