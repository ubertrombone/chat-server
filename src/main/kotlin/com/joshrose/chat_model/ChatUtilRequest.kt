package com.joshrose.chat_model

import com.joshrose.chat_model.Functions.INDIVIDUAL
import com.joshrose.chat_model.Visibility.PRIVATE
import com.joshrose.util.Username
import kotlinx.serialization.Serializable

@Serializable
data class ChatUtilRequest(
    val function: String,
    val sender: String,
    val recipient: String,
    val message: String
)

@Serializable
data class ChatRequest(
    val function: Functions = INDIVIDUAL,
    val visibility: Visibility = PRIVATE,
    val recipient: Username? = null,
    val message: String
)
