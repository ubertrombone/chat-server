package com.joshrose.util

import com.joshrose.Constants.INVALID_CHARS_USERNAME
import com.joshrose.Constants.REQUIREMENT_MAX
import com.joshrose.Constants.USERNAME_EXISTS
import com.joshrose.Constants.USERNAME_TOO_LONG
import com.joshrose.plugins.dao
import com.joshrose.requests.UpdateUsernameRequest

suspend fun validateUpdateNewUsername(request: UpdateUsernameRequest) = with (request) {
    when {
        newUsername.name.length > REQUIREMENT_MAX -> USERNAME_TOO_LONG
        newUsername.name.any { !it.isLetterOrDigit() } -> INVALID_CHARS_USERNAME
        dao.usernameExists(newUsername) -> USERNAME_EXISTS
        else -> null
    }
}