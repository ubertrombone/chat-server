package com.joshrose.chat_model

import com.joshrose.util.Username
import kotlinx.serialization.Serializable

@Serializable
data class FriendChatMessage(
    val sender: Username,
    val recipient: Username,
    val message: String,
    val error: String? = null
)