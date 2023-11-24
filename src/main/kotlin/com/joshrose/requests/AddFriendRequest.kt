package com.joshrose.requests

import kotlinx.serialization.Serializable

@Serializable
data class AddFriendRequest(
    val id: Int,
    val otherUser: String
)
