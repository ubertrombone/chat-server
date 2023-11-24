package com.joshrose.requests

import kotlinx.serialization.Serializable

@Serializable
data class AccountRequest(
    val email: String,
    val password: String,
    val username: String
)
