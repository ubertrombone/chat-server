package com.joshrose

import io.ktor.websocket.*
import java.util.concurrent.atomic.AtomicInteger

class Connection(val session: DefaultWebSocketSession) {
    companion object {
        val lastId = AtomicInteger(0)
    }

    var name: String? = null
        private set

    fun setUsername(username: String?) {
        name = username ?: "User${lastId.getAndIncrement()}"
    }
}