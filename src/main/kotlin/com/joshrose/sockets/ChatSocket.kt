package com.joshrose.sockets

import com.joshrose.Connection
import com.joshrose.chat_model.ChatUtilRequest
import com.joshrose.util.toUsername
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

// TODO: Thoughts
//  1. Single chat websocket that includes all users
//      - It can be used to see who is online/offline
//      - Users can then send messages to one another without needing to make a request for the other user to join new socket
//      - Client will need to handle directing messages based on some form of markup.
//      - For group chats, the downside is how to limit who can be added or not, requires
//          -- rethinking of public/private groups
//          -- How a user can access a private group - i.e., be added or already added
fun Route.chatSocket() {
    val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())

    authenticate {
        webSocket("/chat") {
            val thisConnection = Connection(
                session = this,
                name = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString().toUsername()
            )
            connections += thisConnection

            try {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {

                    }
                }
            } finally {
                println("Removing $thisConnection!")
                val exitMessage = "${thisConnection.name} has left"
                connections -= thisConnection
                connections.forEach { it.session.send(exitMessage) }
            }
        }
    }

    authenticate {
        webSocket("/chat/{from}/{to}") {
            if (connections.size >= 2) return@webSocket

            val thisConnection = Connection(
                session = this,
                name = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString().toUsername()
            )
            connections += thisConnection

            try {
                send("Connected!")
                connections.forEach { it.session.send("${thisConnection.name} has joined") }

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
                val exitMessage = "${thisConnection.name} has left"
                connections -= thisConnection
                connections.forEach { it.session.send(exitMessage) }
            }
        }
    }

//    webSocket("/chat/{username}/{sender}") {
//        if (connections.size >= 2) return@webSocket
//
//        val thisConnection = Connection(this)
//        thisConnection.setUsername(null)
//        connections += thisConnection
//
//        try {
//            send("Connected!")
//            for (frame in incoming) {
//                frame as? Frame.Text ?: continue
//
//                try {
//                    val message = Json.decodeFromString<ChatUtilRequest>(frame.readText())
//                    if (message.function == "Chat")
//                        connections.forEach { it.session.send(Json.encodeToString(message)) }
//                } catch (e: IllegalArgumentException) {
//                    println("MESSAGE: ${e.localizedMessage}")
//                    send("Invalid Request!")
//                    continue
//                }
//            }
//        } catch (e: Exception) {
//            println(e.localizedMessage)
//        } finally {
//            println("Removing $thisConnection!")
//            val exitMessage = "${thisConnection.name} has left"
//            connections -= thisConnection
//            connections.forEach { it.session.send(exitMessage) }
//        }
//    }
}