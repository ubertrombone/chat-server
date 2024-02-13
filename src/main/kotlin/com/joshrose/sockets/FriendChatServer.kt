package com.joshrose.sockets

import com.joshrose.Connection
import com.joshrose.chat_model.FriendChatMessage
import com.joshrose.models.Cache
import com.joshrose.plugins.cacheDao
import com.joshrose.plugins.dao
import com.joshrose.responses.SendFriendChatResponse
import com.joshrose.responses.SimpleResponse
import com.joshrose.util.Username
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

class FriendChatServer(
    private val session: DefaultWebSocketServerSession,
    private val chatId: Int
) : Server {
    private val json = Json { prettyPrint = true }
    private val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())

    override suspend fun establishConnection(username: Username): Connection =
        Connection(session, username).also { addConnection(it) }
    override suspend fun addConnection(connection: Connection) = connections.add(connection)
    override suspend fun removeConnection(connection: Connection) = connections.remove(connection)

    override suspend fun sendMessage(message: FriendChatMessage): SimpleResponse =
        connections.findConnectionByUsername(message.recipient.name)?.let { handleOnlineUser(it, message) }
            ?: SimpleResponse(false, "Could not find user: ${message.recipient}")

    // TODO: A function that responds if other user has successfully been added to connections?

    private suspend fun handleOnlineUser(connection: Connection, message: FriendChatMessage): SimpleResponse =
        if (sendMessageToUser(connection, message)) cacheMessage(connection, message)
        else SimpleResponse(false, "Failed to send the message")

    private suspend fun sendMessageToUser(connection: Connection, message: FriendChatMessage): Boolean =
        runCatching { connection.session.send(Json.encodeToString(message)) }.isSuccess

    private suspend fun cacheMessage(connection: Connection, message: FriendChatMessage): SimpleResponse =
        if (dao.user(connection.name)!!.cache)
            writeToCache(connection, message)
                ?.let { SimpleResponse(true, "Message cached.") }
                ?: SimpleResponse(false, "There was a problem caching this message.")
        else SimpleResponse(true, "User doesn't permit caching.")

    private fun Set<Connection?>.findConnectionByUsername(username: String) = find { it?.name?.name == username }

    private suspend fun writeToCache(connection: Connection, message: FriendChatMessage): Cache? =
        with(Pair(dao.userID(message.sender)!!, dao.userID(message.recipient)!!)) {
            cacheDao.add(
                message = message.message,
                sender = first,
                primaryUser = dao.userID(connection.name)!!,
                error = null,
                chat = chatId
            )
        }

    override suspend fun handleIncomingFrames(connection: Connection) =
        session.incoming.consumeEach { frame ->
            if (frame is Frame.Text) with (receiveMessage(frame.readText())) {
                connection.session.send(json.encodeToString<SendFriendChatResponse>(
                    SendFriendChatResponse(successful = processMessage(this).successful, message = this)
                ))
            }
        }

    private fun receiveMessage(frame: String): FriendChatMessage =
        json.decodeFromString<FriendChatMessage>(frame)
            .also { session.call.application.environment.log.info("Chat Message: ${json.encodeToString(it)}") }

    private suspend fun processMessage(message: FriendChatMessage): SimpleResponse =
        sendMessage(message)
            .also { session.call.application.environment.log.info("Chat response: ${json.encodeToString(it)}") }
}