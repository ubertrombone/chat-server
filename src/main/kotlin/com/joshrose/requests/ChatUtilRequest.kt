package com.joshrose.requests

import kotlinx.serialization.Serializable

@Serializable
data class ChatUtilRequest(
    val function: String,
    val sender: String,
    val recipient: String,
    val message: String
)
