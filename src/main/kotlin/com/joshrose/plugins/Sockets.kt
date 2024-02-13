package com.joshrose.plugins

import com.joshrose.sockets.chatRequest
import com.joshrose.sockets.chatSocket
import com.joshrose.sockets.friendChatSocket
import com.joshrose.sockets.groupChatRequest
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json
import java.time.Duration

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
    }
    routing {
        chatRequest()
        chatSocket()
        friendChatSocket()
        groupChatRequest()
    }
}
