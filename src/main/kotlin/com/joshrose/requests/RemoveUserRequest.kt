package com.joshrose.requests

import kotlinx.serialization.Serializable

@Serializable
data class RemoveUserRequest(val id: Int, val delete: Boolean)
