package com.joshrose.sockets

import com.joshrose.Connection
import com.joshrose.Constants.FRIEND_CHAT_SOCKET_ROUTE
import com.joshrose.chat_model.FriendChatMessage
import com.joshrose.responses.SendFriendChatResponse
import com.joshrose.responses.SimpleResponse
import com.joshrose.util.Username
import com.joshrose.util.toUsername
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.encodeToString

fun Route.friendChatSocket() {
    authenticate {
        webSocket(FRIEND_CHAT_SOCKET_ROUTE) {
            val chatId = call.parameters["chatID"]?.toIntOrNull() ?: return@webSocket
            val server = FriendChatServer(chatId)

            val userPrincipal = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString().toUsername()
            val thisConnection = establishConnection(userPrincipal, server)

            try {
                handleIncomingFrames(thisConnection, server)
            } finally {
                server.removeConnection(thisConnection)
            }
        }
    }
}

private suspend fun DefaultWebSocketServerSession.establishConnection(username: Username, server: FriendChatServer): Connection =
    Connection(session = this, name = username).also { server.addConnection(it) }

private suspend fun DefaultWebSocketServerSession.handleIncomingFrames(connection: Connection, server: FriendChatServer) =
    incoming.consumeEach { frame ->
        if (frame is Frame.Text) with (receiveMessage(frame.readText())) {
            connection.session.send(json.encodeToString<SendFriendChatResponse>(
                SendFriendChatResponse(successful = processMessage(this, server).successful, message = this)
            ))
        }
    }

private fun DefaultWebSocketServerSession.receiveMessage(frame: String): FriendChatMessage =
    json.decodeFromString<FriendChatMessage>(frame)
        .also { call.application.environment.log.info("Chat Message: ${json.encodeToString(it)}") }

private suspend fun DefaultWebSocketServerSession.processMessage(message: FriendChatMessage, server: FriendChatServer): SimpleResponse =
    server.sendMessage(message)
        .also { call.application.environment.log.info("Chat response: ${json.encodeToString(it)}") }