package com.joshrose.requests

import kotlinx.serialization.Serializable

@Serializable
data class UnblockUserRequest(
    val id: Int,
    val otherUser: String
)
