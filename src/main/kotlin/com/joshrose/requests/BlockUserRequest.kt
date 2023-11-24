package com.joshrose.requests

import kotlinx.serialization.Serializable

@Serializable
data class BlockUserRequest(
    val id: Int,
    val otherUser: String
)
