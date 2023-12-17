package com.joshrose.routes

import com.joshrose.plugins.groupChatDao
import com.joshrose.requests.GroupChatNameRequest
import com.joshrose.util.toUsername
import com.joshrose.validations.validateGroupChat
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock

fun Route.groupChatRoute() {

    route("/group_chat") {
        install(RequestValidation) {
            validateGroupChat()
        }

        authenticate {
            get {
                val chats = groupChatDao.allGroupChats()
                call.respond(OK, chats)
            }

            post {
                val request = try {
                    call.receive<GroupChatNameRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@post
                }

                val user = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString().toUsername()
                groupChatDao.addNewGroupChat(
                    name = request.name,
                    creator = user,
                    createdDate = Clock.System.now(),
                    members = emptySet()
                )
                call.respond(OK, "Group Created!")
            }
        }
    }
}