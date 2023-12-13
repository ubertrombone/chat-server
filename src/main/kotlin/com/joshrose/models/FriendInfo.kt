package com.joshrose.models

import com.joshrose.util.Username
import kotlinx.serialization.Serializable

@Serializable
data class FriendInfo(
    val username: Username,
    val isOnline: Boolean,
    val lastOnline: String?
)
