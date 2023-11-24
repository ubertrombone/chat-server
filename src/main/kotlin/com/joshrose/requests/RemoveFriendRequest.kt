package com.joshrose.requests

import kotlinx.serialization.Serializable

@Serializable
data class RemoveFriendRequest(
    val id: Int,
    val otherUser: String
)
