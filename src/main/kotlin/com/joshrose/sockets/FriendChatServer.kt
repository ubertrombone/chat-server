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
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

class FriendChatServer : Server {
    private val json = Json { prettyPrint = true }
    private val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
    private var chatId: Int = -1

    override suspend fun establishConnection(username: Username, chatId: Int, session: DefaultWebSocketServerSession): Connection =
        Connection(session, username).also { updateProperties(it, chatId) }
    override suspend fun addConnection(connection: Connection) = connections.add(connection)
    override suspend fun removeConnection(connection: Connection) = connections.remove(connection)

    private suspend fun updateProperties(connection: Connection, chatId: Int) {
        this.chatId = chatId
        addConnection(connection)
    }

    override suspend fun <T> sendMessage(message: T): SimpleResponse =
        runCatching { waitForUser(message as FriendChatMessage) }
            .getOrElse { SimpleResponse(false, "Invalid message type") }

    private suspend fun waitForUser(message: FriendChatMessage, elapsedTime: Int = 0): SimpleResponse =
        connections.findConnectionByUsername(message.recipient.name)?.let { handleOnlineUser(it, message) }
            ?: when {
                !dao.usernameExists(message.recipient) -> SimpleResponse(false, "Could not find user: ${message.recipient}")
                !dao.user(message.recipient)!!.isOnline -> SimpleResponse(false, "${message.recipient} is offline")
                elapsedTime == 31 -> SimpleResponse(false, "Could not send message to ${message.recipient}")
                else -> {
                    delay(1000)
                    waitForUser(message, elapsedTime + 1)
                }
            }

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

    override suspend fun handleIncomingFrames(connection: Connection, session: DefaultWebSocketServerSession) =
        session.incoming.consumeEach { frame ->
            if (frame is Frame.Text) with (receiveMessage(frame.readText(), session)) {
                connection.session.send(json.encodeToString<SendFriendChatResponse>(
                    SendFriendChatResponse(successful = processMessage(this, session).successful, message = this)
                ))
            }
        }

    private fun receiveMessage(frame: String, session: DefaultWebSocketServerSession): FriendChatMessage =
        json.decodeFromString<FriendChatMessage>(frame)
            .also { session.call.application.environment.log.info("Chat Message: ${json.encodeToString(it)}") }

    private suspend fun processMessage(message: FriendChatMessage, session: DefaultWebSocketServerSession): SimpleResponse =
        sendMessage(message)
            .also { session.call.application.environment.log.info("Chat response: ${json.encodeToString(it)}") }
}