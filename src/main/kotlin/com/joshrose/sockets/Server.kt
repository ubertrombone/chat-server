package com.joshrose.sockets

import com.joshrose.Connection
import com.joshrose.responses.SimpleResponse
import com.joshrose.util.Username
import io.ktor.server.websocket.*

interface Server {
    suspend fun establishConnection(username: Username, chatId: Int, session: DefaultWebSocketServerSession): Connection
    suspend fun addConnection(connection: Connection): Boolean
    suspend fun removeConnection(connection: Connection): Boolean
    suspend fun <T> sendMessage(message: T): SimpleResponse
    suspend fun handleIncomingFrames(connection: Connection, session: DefaultWebSocketServerSession)
}