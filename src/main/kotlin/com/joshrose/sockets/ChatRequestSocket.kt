package com.joshrose.sockets

import com.joshrose.Constants.CHAT_ROUTE
import com.joshrose.util.toUsername
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*

fun Route.chatRequest() {
    val server = ChatRequestServer()

    authenticate {
        webSocket(CHAT_ROUTE) {
            val userPrincipal = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString().toUsername()
            val thisConnection = server.establishConnection(userPrincipal, this)

            try {
                server.handleIncomingFrames(thisConnection, this)
            } finally {
                server.removeConnection(thisConnection)
            }
        }
    }
}