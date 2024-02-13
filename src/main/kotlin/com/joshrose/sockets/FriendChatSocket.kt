package com.joshrose.sockets

import com.joshrose.Constants.FRIEND_CHAT_SOCKET_ROUTE
import com.joshrose.util.toUsername
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*

fun Route.friendChatSocket() {
    authenticate {
        webSocket(FRIEND_CHAT_SOCKET_ROUTE) {
            val chatId = call.parameters["chatID"]?.toIntOrNull() ?: return@webSocket
            val server = FriendChatServer(this, chatId)
            val userPrincipal = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString().toUsername()
            val thisConnection = server.establishConnection(userPrincipal)

            try {
                server.handleIncomingFrames(thisConnection)
            } finally {
                server.removeConnection(thisConnection)
            }
        }
    }
}