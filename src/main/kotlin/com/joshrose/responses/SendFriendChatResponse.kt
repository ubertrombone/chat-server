package com.joshrose.responses

import com.joshrose.chat_model.FriendChatMessage
import kotlinx.serialization.Serializable

@Serializable
data class SendFriendChatResponse(
    val successful: Boolean,
    val message: FriendChatMessage
)
