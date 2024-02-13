package com.joshrose.sockets

import com.joshrose.Connection
import com.joshrose.chat_model.FriendChatMessage
import com.joshrose.responses.SimpleResponse
import com.joshrose.util.Username

interface Server {
    suspend fun establishConnection(username: Username): Connection
    suspend fun addConnection(connection: Connection): Boolean
    suspend fun removeConnection(connection: Connection): Boolean
    suspend fun sendMessage(message: FriendChatMessage): SimpleResponse
    suspend fun handleIncomingFrames(connection: Connection)
}