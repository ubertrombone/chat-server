package com.joshrose.sockets

import com.joshrose.Connection
import com.joshrose.chat_model.FriendChatMessage
import com.joshrose.chat_model.OpenChatRequest
import com.joshrose.plugins.chatDao
import com.joshrose.plugins.dao
import com.joshrose.responses.ChatEndPointResponse
import com.joshrose.responses.SimpleResponse
import com.joshrose.util.Username
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

class ChatRequestServer(private val session: DefaultWebSocketServerSession) {
    private val json = Json { prettyPrint = true }
    private val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())

    fun establishConnection(username: Username): Connection =
        Connection(session, username).also { addConnection(it) }
    private fun addConnection(connection: Connection): Boolean = connections.add(connection)
    fun removeConnection(connection: Connection): Boolean = connections.remove(connection)

    private suspend fun sendRequest(request: OpenChatRequest): ChatEndPointResponse =
        connections.findConnectionByUsername(request.recipient)?.let { handleOnlineUser(it, request) }
            ?: ChatEndPointResponse(false, -1)

    private suspend fun handleOnlineUser(connection: Connection, request: OpenChatRequest): ChatEndPointResponse =
        if (sendRequestToUser(connection, request)) ChatEndPointResponse(true, getOrCreateChat(request))
        else ChatEndPointResponse(false, -1)

    // TODO: We want to be sending the chatID to the recipient not the initial request.
    private suspend fun sendRequestToUser(connection: Connection, request: OpenChatRequest): Boolean =
        runCatching { connection.session.send(Json.encodeToString(request)) }.isSuccess

    private suspend fun Set<Connection?>.findConnectionByUsername(user: Int) =
        with(dao.user(user)) { find { it?.name == this?.username } }

    suspend fun handleIncomingFrames(connection: Connection) =
        session.incoming.consumeEach { frame ->
            if (frame is Frame.Text) with (receiveRequest(frame.readText())) {
                connection.session.send(json.encodeToString<ChatEndPointResponse>(
                    ChatEndPointResponse(success = processRequest(this).success, chatId = )
                ))
            }
        }

    private suspend fun getOrCreateChat(request: OpenChatRequest): Int =
        chatDao.chat(request.sender, request.recipient)?.id ?: chatDao.addChat(request.sender, request.recipient)!!.id

    private fun receiveRequest(frame: String): OpenChatRequest =
        json.decodeFromString<OpenChatRequest>(frame)
            .also { session.call.application.environment.log.info("Chat Message: ${json.encodeToString(it)}") }

    private suspend fun processRequest(request: OpenChatRequest): ChatEndPointResponse =
        sendRequest(request)
            .also { session.call.application.environment.log.info("Chat response: ${json.encodeToString(it)}") }
}