package com.joshrose.routes

import com.joshrose.plugins.cacheDao
import com.joshrose.util.receiveOrNull
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.chatsRoute() {
    route("/chat_actions") {
        authenticate {
            post("/delete_message") {
                val message = call.receiveOrNull<Int>() ?: return@post

                if (cacheDao.delete(message)) call.respond(OK, "Message successfully deleted!")
                else call.respond(BadRequest, "Error deleting message!")
            }
        }
    }
}