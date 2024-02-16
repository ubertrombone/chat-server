package com.joshrose.sockets

import com.joshrose.Connection
import com.joshrose.chat_model.OpenChatRequest
import com.joshrose.plugins.chatDao
import com.joshrose.plugins.dao
import com.joshrose.responses.ChatEndPointResponse
import com.joshrose.util.Username
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

class ChatRequestServer {
    private val json = Json { prettyPrint = true }
    private val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())

    suspend fun establishConnection(username: Username, session: DefaultWebSocketServerSession): Connection =
        Connection(session, username).also { addConnection(it) }
    private suspend fun addConnection(connection: Connection): Boolean =
        connections.add(connection).also { updateUserOnline(connection, true) }
    suspend fun removeConnection(connection: Connection): Boolean =
        connections.remove(connection).also { updateUserOnline(connection, false) }

    private suspend fun updateUserOnline(connection: Connection, isOnline: Boolean) =
        dao.editUser(user = dao.user(connection.name)!!.copy(isOnline = isOnline, lastOnline = Clock.System.now()))

    private suspend fun sendRequest(request: OpenChatRequest, chatId: Int): ChatEndPointResponse =
        connections.findConnectionByUsername(request.recipient)?.let { handleOnlineUser(it, chatId) }
            ?: ChatEndPointResponse(false, -1)

    private suspend fun handleOnlineUser(connection: Connection, chatId: Int): ChatEndPointResponse =
        if (sendRequestToUser(connection, chatId)) ChatEndPointResponse(true, chatId)
        else ChatEndPointResponse(false, -1)

    private suspend fun sendRequestToUser(connection: Connection, chatId: Int): Boolean =
        runCatching { connection.session.send(Json.encodeToString(ChatEndPointResponse(true, chatId))) }.isSuccess

    private suspend fun Set<Connection?>.findConnectionByUsername(user: Int) =
        with(dao.user(user)) { find { it?.name == this?.username } }

    suspend fun handleIncomingFrames(connection: Connection, session: DefaultWebSocketServerSession) =
        session.incoming.consumeEach { frame ->
            if (frame is Frame.Text) with (receiveRequest(frame.readText(), session)) {
                connection.session.send(json.encodeToString<ChatEndPointResponse>(
                    this?.let { req -> processRequest(req, getOrCreateChat(req), session) }
                        ?: ChatEndPointResponse(false, -1)
                ))
            }
        }

    private suspend fun getOrCreateChat(request: OpenChatRequest): Int =
        chatDao.chat(request.sender, request.recipient)?.id ?: chatDao.addChat(request.sender, request.recipient)!!.id

    private fun receiveRequest(frame: String, session: DefaultWebSocketServerSession): OpenChatRequest? =
        runCatching { json.decodeFromString<OpenChatRequest>(frame) }.getOrNull()
            .also { session.call.application.environment.log.info("Chat Message: ${it?.let { json.encodeToString(it) } ?: "null"}") }

    private suspend fun processRequest(request: OpenChatRequest, chatId: Int, session: DefaultWebSocketServerSession): ChatEndPointResponse =
        sendRequest(request, chatId)
            .also { session.call.application.environment.log.info("Chat response: ${json.encodeToString(it)}") }
}