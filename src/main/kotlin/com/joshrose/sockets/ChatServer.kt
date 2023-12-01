package com.joshrose.sockets

import com.joshrose.Connection
import io.ktor.websocket.*
import java.util.*

class ChatServer {
    val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())

    suspend fun newChatter(connection: Connection) { connections += connection }
    suspend fun removeChatter(connection: Connection) { connections -= connection }

    private suspend fun broadcast(message: String) = connections.forEach { it.session.send(message) }
}