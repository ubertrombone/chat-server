package com.joshrose.requests

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUsernameRequest(val id: Int, val newUsername: String)
