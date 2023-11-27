package com.joshrose.requests

import kotlinx.serialization.Serializable

@Serializable
data class UpdatePasswordRequest(
    val oldPassword: String,
    val newPassword: String,
    val newPasswordConfirm: String
)
