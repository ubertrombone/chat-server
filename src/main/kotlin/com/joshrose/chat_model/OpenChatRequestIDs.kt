package com.joshrose.chat_model

import kotlinx.serialization.Serializable

@Serializable
data class OpenChatRequestIDs(
    val sender: Int,
    val recipient: Int
)
