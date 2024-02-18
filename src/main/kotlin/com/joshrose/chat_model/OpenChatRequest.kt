package com.joshrose.chat_model

import com.joshrose.util.Username
import kotlinx.serialization.Serializable

@Serializable
data class OpenChatRequest(
    val sender: Username,
    val recipient: Username
)
