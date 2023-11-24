package com.joshrose.requests

import kotlinx.serialization.Serializable

@Serializable
data class LogoutRequest(val id: Int, val isOnline: Boolean)
