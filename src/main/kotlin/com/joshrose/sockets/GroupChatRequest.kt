package com.joshrose.sockets

import io.ktor.server.routing.*

fun Route.groupChatRequest() {
//    val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
//
//    webSocket("/group/{id}/{name}") {
//        val thisConnection = Connection(this)
//        thisConnection.setUsername(null)
//        connections += thisConnection
//        val group = groupChatDao.groupChat(call.parameters["id"]!!.toInt())
//        groupChatDao.editGroupChat(group!!.copy(population = connections.size))
//        try {
//            send("Connected!")
//            for (frame in incoming) {
//                frame as? Frame.Text ?: continue
//                try {
//                    val message = Json.decodeFromString<ChatUtilRequest>(frame.readText())
//                    if (message.function == "Group" || message.function == "Request")
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
//            connections -= thisConnection
//            groupChatDao.editGroupChat(group.copy(population = connections.size))
//            if (connections.size == 0) groupChatDao.deleteGroupChat(call.parameters["id"]!!.toInt())
//        }
//    }
}