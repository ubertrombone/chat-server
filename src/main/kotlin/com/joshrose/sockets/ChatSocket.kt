package com.joshrose.sockets

import com.joshrose.Connection
import com.joshrose.requests.ChatUtilRequest
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

fun Route.chatSocket() {
    val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())

    webSocket("/chat/{username}/{sender}") {
        if (connections.size >= 2) return@webSocket

        val thisConnection = Connection(this)
        thisConnection.setUsername(null)
        connections += thisConnection

        try {
            send("Connected!")
            for (frame in incoming) {
                frame as? Frame.Text ?: continue

                try {
                    val message = Json.decodeFromString<ChatUtilRequest>(frame.readText())
                    if (message.function == "Chat")
                        connections.forEach { it.session.send(Json.encodeToString(message)) }
                } catch (e: IllegalArgumentException) {
                    println("MESSAGE: ${e.localizedMessage}")
                    send("Invalid Request!")
                    continue
                }
            }
        } catch (e: Exception) {
            println(e.localizedMessage)
        } finally {
            println("Removing $thisConnection!")
            connections -= thisConnection
        }
    }
}