package com.joshrose.sockets

import com.joshrose.Constants.CHAT_ROUTE
import com.joshrose.util.toUsername
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*

fun Route.chatRequest() {
    authenticate {
        webSocket(CHAT_ROUTE) {
            val server = ChatRequestServer(this)
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