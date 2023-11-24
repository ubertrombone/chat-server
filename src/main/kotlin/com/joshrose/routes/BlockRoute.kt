package com.joshrose.routes

import com.joshrose.plugins.Security
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
                val id = call.principal<Security>()!!.id
                val blockList = dao.user(id)!!.blockedList
                call.respond(OK, blockList ?: "")
            }

            post {
                val request = try {
                    call.receive<BlockUserRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@post
                }

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