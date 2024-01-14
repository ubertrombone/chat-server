package com.joshrose.models

import com.joshrose.util.Username
import kotlinx.serialization.Serializable

@Serializable
data class FriendRequestConverted(
    val id: Int,
    val requesterUsername: Username,
    val toUsername: Username
)
