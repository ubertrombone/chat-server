package com.joshrose.requests

import com.joshrose.util.Username
import kotlinx.serialization.Serializable

// TODO: Make password and inline class too?
@Serializable
data class AccountRequest(
    val username: Username,
    val password: String,
)
