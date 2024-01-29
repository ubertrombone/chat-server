package com.joshrose.responses

import com.joshrose.chat_model.ChatMessage
import kotlinx.serialization.Serializable

@Serializable
data class SendChatResponse(
    val successful: Boolean,
    val message: ChatMessage
)
