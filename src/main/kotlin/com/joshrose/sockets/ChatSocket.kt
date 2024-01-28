package com.joshrose.sockets

import com.joshrose.Connection
import com.joshrose.chat_model.ChatMessage
import com.joshrose.chat_model.Functions.*
import com.joshrose.plugins.groupChatDao
import com.joshrose.responses.SimpleResponse
import com.joshrose.util.toUsername
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val CHAT_ROUTE = "/chat"

fun Route.chatSocket() {
    val server = ChatServer()

    authenticate {
        webSocket(CHAT_ROUTE) {
            val userPrincipal = call.principal<JWTPrincipal>()!!
            val thisConnection = establishConnection(userPrincipal, server)

            try {
                handleIncomingFrames(server, thisConnection)
            } finally {
                server.removeConnection(thisConnection)
            }
        }
    }
}

suspend fun DefaultWebSocketServerSession.establishConnection(principal: JWTPrincipal, server: ChatServer): Connection {
    val username = principal.payload.getClaim("username").asString().toUsername()
    val connection = Connection(session = this, name = username)
    server.addConnection(connection)
    return connection
}

suspend fun DefaultWebSocketServerSession.handleIncomingFrames(server: ChatServer, connection: Connection) =
    incoming.consumeEach { frame ->
        if (frame is Frame.Text) {
            val message = Json.decodeFromString<ChatMessage>(frame.readText())
            val response = delegateMessageProcessing(message, server, connection)
            call.application.environment.log.info(Json.encodeToString(response))
            if (!response.successful) {
                val err = message.copy(function = ERROR, error = response.message)
                connection.session.send(Json.encodeToString<ChatMessage>(err))
            }
        }
    }

suspend fun delegateMessageProcessing(message: ChatMessage, server: ChatServer, connection: Connection) =
    when (message.function) {
        GROUP -> server.messageGroup(message)
        INDIVIDUAL -> server.sendTo(message)
        LEAVE -> {
            val group = groupChatDao.groupChat(message.recipientOrGroup)
            server.leaveGroup(group, connection)
        }
        JOIN -> {
            val group = groupChatDao.groupChat(message.recipientOrGroup)
            server.joinGroup(group, connection)
        }
        ERROR -> { SimpleResponse(true, "") }
    }
