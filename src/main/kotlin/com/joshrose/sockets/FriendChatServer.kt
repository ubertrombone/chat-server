package com.joshrose.sockets

import com.joshrose.Connection
import com.joshrose.chat_model.FriendChatMessage
import com.joshrose.models.Cache
import com.joshrose.models.Chat
import com.joshrose.plugins.cacheDao
import com.joshrose.plugins.chatDao
import com.joshrose.plugins.dao
import com.joshrose.responses.SimpleResponse
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

class FriendChatServer(private val chatId: Int?) : Server {
    private val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())

    override suspend fun addConnection(connection: Connection) = connections.add(connection)
    override suspend fun removeConnection(connection: Connection) = connections.remove(connection)

    override suspend fun sendMessage(message: FriendChatMessage): SimpleResponse =
        connections.findConnectionByUsername(message.recipient.name)?.let { handleOnlineUser(it, message) }
            ?: SimpleResponse(false, "Could not find user: ${message.recipient}")

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

    private suspend fun getOrCreateChat(userOne: Int, userTwo: Int): Chat =
        chatDao.chat(userOne, userTwo) ?: chatDao.addChat(userOne, userTwo)!!

    private suspend fun writeToCache(connection: Connection, message: FriendChatMessage): Cache? =
        with(Pair(dao.userID(message.sender)!!, dao.userID(message.recipient)!!)) {
            cacheDao.add(
                message = message.message,
                sender = first,
                primaryUser = dao.userID(connection.name)!!,
                error = null,
                chat = getOrCreateChat(first, second).id
            )
        }
}