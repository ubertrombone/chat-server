package com.joshrose.sockets

import io.ktor.server.routing.*

fun Route.chatRequest() {
//    val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
//
//    webSocket("/chat/{username}") {
//        val thisConnection = Connection(this)
//        thisConnection.setUsername(null)
//        connections += thisConnection
//        try {
//            send("${thisConnection.name} connected! There are ${connections.count()} users here.")
//            for (frame in incoming) {
//                frame as? Frame.Text ?: continue
//                try {
//                    val message = Json.decodeFromString<ChatUtilRequest>(frame.readText())
//                    when (message.function) {
//                        "Request" -> connections.forEach { it.session.send(Json.encodeToString(message)) }
//                        "GroupInvite" -> connections.forEach { it.session.send(Json.encodeToString(message)) }
//                    }
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
//            connections -= thisConnection
//        }
//    }
}