package com.joshrose.sockets

import com.joshrose.Connection
import com.joshrose.responses.SimpleResponse
import com.joshrose.util.Username

interface Server {
    suspend fun establishConnection(username: Username): Connection
    suspend fun addConnection(connection: Connection): Boolean
    suspend fun removeConnection(connection: Connection): Boolean
    suspend fun <T> sendMessage(message: T): SimpleResponse
    suspend fun handleIncomingFrames(connection: Connection)
}