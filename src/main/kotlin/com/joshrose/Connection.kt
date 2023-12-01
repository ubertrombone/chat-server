package com.joshrose

import com.joshrose.util.Username
import io.ktor.websocket.*

data class Connection(val session: DefaultWebSocketSession, val name: Username)