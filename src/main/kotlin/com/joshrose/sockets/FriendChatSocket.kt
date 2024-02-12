package com.joshrose.sockets

import com.joshrose.Constants.FRIEND_CHAT_SOCKET_ROUTE
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json

fun Route.friendChatSocket() {
    val json = Json { prettyPrint = true }

    authenticate {
        webSocket(FRIEND_CHAT_SOCKET_ROUTE) {
            val userPrincipal = call.principal<JWTPrincipal>()!!
            val chatId = call.parameters["chatID"]?.toIntOrNull()
        }
    }
}