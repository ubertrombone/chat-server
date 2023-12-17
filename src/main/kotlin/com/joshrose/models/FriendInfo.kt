package com.joshrose.models

import com.joshrose.util.Username
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class FriendInfo(
    val username: Username,
    val isOnline: Boolean,
    val lastOnline: Instant?
)
