package com.joshrose.requests

import com.joshrose.util.Username
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUsernameRequest(val newUsername: Username)
