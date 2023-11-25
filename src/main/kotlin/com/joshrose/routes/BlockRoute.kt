package com.joshrose.routes

import com.joshrose.plugins.dao
import com.joshrose.requests.BlockUserRequest
import com.joshrose.requests.UnblockUserRequest
import com.joshrose.validations.validateBlockRequest
import com.joshrose.validations.validateUnblockRequest
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
import java.time.LocalDateTime

fun Route.blockRoute() {
    route("/block") {
        install(RequestValidation) {
            validateBlockRequest()
            validateUnblockRequest()
        }

        authenticate {
            get {
                val user = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString()
                val blockList = dao.user(user)!!.blockedList
                call.respond(OK, blockList ?: "")
            }

            post {
                val request = try {
                    call.receive<BlockUserRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@post
                }

                // TODO:
                //  1. ValidateBlockRequest -- should only check if otherUser exists
                //  2. In route body, check if otherUser is already in current User's block list

                val user = dao.user(request.id)!!
                val blockedList = user.blockedList?.split(";")
                dao.editUser(
                    user = user.copy(
                        lastOnline = LocalDateTime.now(),
                        friendList = blockedList?.let { "$it;${request.otherUser}" } ?: request.otherUser
                    )
                )
                call.respond(Accepted, "${request.otherUser} is blocked!")
            }

            post("/unblock") {
                val request = try {
                    call.receive<UnblockUserRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@post
                }

                // TODO: Validate block isn't required; use logic here instead

                val user = dao.user(request.id)!!
                val blockedList = user.blockedList!!.split(";")
                dao.editUser(
                    user = user.copy(
                        lastOnline = LocalDateTime.now(),
                        friendList = blockedList.minus(request.otherUser).joinToString(";")
                    )
                )
                call.respond(Accepted, "${request.otherUser} is unblocked!")
            }
        }
    }
}