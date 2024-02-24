package com.joshrose.responses

import com.joshrose.chat_model.OpenChatRequest
import kotlinx.serialization.Serializable

@Serializable
data class ChatEndPointResponse(
    val success: Boolean,
    val chatId: Int,
    val request: OpenChatRequest?
)
