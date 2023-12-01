package com.joshrose.sockets

import com.joshrose.Connection
import com.joshrose.chat_model.ChatMessage
import com.joshrose.chat_model.Functions
import com.joshrose.util.toUsername
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json

const val CHAT_ROUTE = "/chat"

fun Route.chatSocket() {
    val server = ChatServer()

    authenticate {
        webSocket(CHAT_ROUTE) {
            val userPrincipal = call.principal<JWTPrincipal>()!!
            val thisConnection = establishConnection(userPrincipal, server)

            try {
                handleIncomingFrames(server)
            } finally {
                server.removeConnection(thisConnection)
            }
        }
    }
}

suspend fun DefaultWebSocketSession.establishConnection(principal: JWTPrincipal, server: ChatServer): Connection {
    val username = principal.payload.getClaim("username").asString().toUsername()
    val connection = Connection(session = this, name = username)
    server.addConnection(connection)
    return connection
}

suspend fun DefaultWebSocketSession.handleIncomingFrames(server: ChatServer) =
    incoming.consumeEach { frame ->
        if (frame is Frame.Text) {
            val message = Json.decodeFromString<ChatMessage>(frame.readText())
            delegateMessageProcessing(message, server)
        }
    }

suspend fun delegateMessageProcessing(message: ChatMessage, server: ChatServer) =
    when (message.function) {
        Functions.GROUP -> server.messageGroup(message)
        Functions.INDIVIDUAL -> server.sendTo(message)
    }
