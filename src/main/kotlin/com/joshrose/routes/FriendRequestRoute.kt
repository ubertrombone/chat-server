package com.joshrose.routes

import com.joshrose.plugins.dao
import com.joshrose.plugins.friendRequestDao
import com.joshrose.requests.CancelFriendRequestRequest
import com.joshrose.requests.SendRequestRequest
import com.joshrose.responses.SimpleResponse
import com.joshrose.validations.validateCancelFriendRequest
import com.joshrose.validations.validateSendRequest
import io.ktor.http.HttpStatusCode.Companion.Accepted
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.friendRequestRoute() {
    route("/friend_request") {
        install(RequestValidation) {
            validateSendRequest()
            validateCancelFriendRequest()
        }

        authenticate {
            get("/sent_friend_requests") {
                val user = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString()
                val id = dao.loginUser(user)
                val sentRequests = friendRequestDao.sentFriendRequests(id)
                if (sentRequests.isNotEmpty()) call.respond(OK, sentRequests)
                else call.respond(OK, SimpleResponse(false, "No friend requests"))
            }

            get("/received_friend_requests") {
                val user = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString()
                val id = dao.loginUser(user)
                val receivedRequests = friendRequestDao.receivedFriendRequests(id)
                if (receivedRequests.isNotEmpty()) call.respond(OK, receivedRequests)
                else call.respond(OK, SimpleResponse(false, "No friend requests"))
            }

            post {
                val request = try {
                    call.receive<SendRequestRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@post
                }

                // TODO: this one is a mess, let's move most validation to route body.

                friendRequestDao.addNewFriendRequest(requesterId = request.requesterId, toId = request.toId)
                call.respond(Accepted, "Request Sent!")
            }

            post("/cancel_request") {
                val request = try {
                    call.receive<CancelFriendRequestRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@post
                }

                // TODO: ValidateCancelFriendRequest not needed, result of removeFriendRequest tells us the same thing

                friendRequestDao.removeFriendRequest(request.id)
                call.respond(OK, "Request cancelled!")
            }
        }
    }
}