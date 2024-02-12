package com.joshrose.sockets

import com.joshrose.Connection
import com.joshrose.chat_model.FriendChatMessage
import com.joshrose.responses.SimpleResponse

interface Server {
    suspend fun addConnection(connection: Connection): Boolean
    suspend fun removeConnection(connection: Connection): Boolean
    suspend fun sendMessage(message: FriendChatMessage): SimpleResponse
}