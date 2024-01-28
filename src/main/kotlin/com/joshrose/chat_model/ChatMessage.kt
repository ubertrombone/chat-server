package com.joshrose.chat_model

import com.joshrose.chat_model.Functions.INDIVIDUAL
import com.joshrose.util.Username
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val function: Functions = INDIVIDUAL,
    val sender: Username,
    val recipientOrGroup: String,
    val message: String,
    val error: String? = null
)
