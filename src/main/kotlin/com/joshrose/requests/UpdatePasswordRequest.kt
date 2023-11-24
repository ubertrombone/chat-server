package com.joshrose.requests

import kotlinx.serialization.Serializable

@Serializable
data class UpdatePasswordRequest(
    val id: Int,
    val oldPassword: String,
    val newPassword: String,
    val newPasswordConfirm: String
)
