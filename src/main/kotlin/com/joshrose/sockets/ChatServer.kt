package com.joshrose.sockets

import com.joshrose.Connection
import com.joshrose.chat_model.ChatMessage
import com.joshrose.chat_model.Functions.GROUP
import com.joshrose.plugins.dao
import com.joshrose.plugins.groupChatDao
import com.joshrose.responses.SimpleResponse
import com.joshrose.util.toUsername
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.util.*

class ChatServer {
    private val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())

    suspend fun addConnection(connection: Connection) {
        connections += connection

        val user = dao.user(connection.name)!!
        dao.editUser(user = user.copy(isOnline = true))
    }
    suspend fun removeConnection(connection: Connection) {
        connections -= connection

        val user = dao.user(connection.name)!!
        dao.editUser(
            user = user.copy(
                isOnline = false,
                lastOnline = LocalDateTime.now()
            )
        )

        groupChatDao.allGroupChats().filter { it.members.contains(connection.name) }.forEach { group ->
            with(group.members.minus(connection.name)) {
                if (isEmpty()) groupChatDao.deleteGroupChat(group.name)
                else groupChatDao.editGroupChat(groupChat = group.copy(members = this))
            }

            messageGroup(
                ChatMessage(
                    function = GROUP,
                    sender = connection.name,
                    recipientOrGroup = group.name,
                    message = "${connection.name} has left"
                )
            )
        }
    }
    suspend fun sendTo(message: ChatMessage): SimpleResponse =
        dao.user(message.recipientOrGroup.toUsername())?.let { username ->
            if (username.isOnline) handleOnlineUser(message)
            else SimpleResponse(false, "${username.username} is offline.")
        } ?: SimpleResponse(false, "Could not find user: ${message.recipientOrGroup}")

    private suspend fun handleOnlineUser(message: ChatMessage): SimpleResponse =
        if (sendMessageToUser(message)) SimpleResponse(true, "Sent successfully")
        else SimpleResponse(false, "Failed to send the message")

    private suspend fun sendMessageToUser(message: ChatMessage): Boolean =
        connections.findConnectionByUsername(message.recipientOrGroup)?.session?.send(Json.encodeToString(message)) != null

    private fun Set<Connection?>.findConnectionByUsername(username: String) = find { it?.name?.name == username }

    suspend fun messageGroup(message: ChatMessage): SimpleResponse =
        with (groupChatDao.groupChat(message.recipientOrGroup)) {
            when {
                this == null -> SimpleResponse(false, "Group not found")
                members.isEmpty() -> SimpleResponse(false, "Group has no members")
                else -> {
                    connections
                        .filter { members.contains(it.name) }
                        .forEach { it.session.send(Json.encodeToString(message)) }
                    SimpleResponse(true, "Sent successfully")
                }
            }
        }
}