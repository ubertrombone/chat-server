package com.joshrose.util

import com.joshrose.Constants.INCORRECT_PASSWORD
import com.joshrose.Constants.INVALID_CHARS_PASSWORD
import com.joshrose.Constants.PASSWORDS_DONT_MATCH
import com.joshrose.Constants.PASSWORD_LONG
import com.joshrose.Constants.PASSWORD_MUST_BE_NEW
import com.joshrose.Constants.PASSWORD_REQUIRED_CHARS
import com.joshrose.Constants.PASSWORD_REQUIREMENT_MIN
import com.joshrose.Constants.PASSWORD_SHORT
import com.joshrose.Constants.REQUIREMENT_MAX
import com.joshrose.plugins.dao
import com.joshrose.requests.UpdatePasswordRequest

suspend fun validateUpdatePassword(username: Username, request: UpdatePasswordRequest) = with (request) {
    when {
        !dao.checkPassword(username, oldPassword) -> INCORRECT_PASSWORD
        newPassword != newPasswordConfirm -> PASSWORDS_DONT_MATCH
        newPassword == oldPassword -> PASSWORD_MUST_BE_NEW
        newPassword.length < PASSWORD_REQUIREMENT_MIN -> PASSWORD_SHORT
        newPassword.length > REQUIREMENT_MAX -> PASSWORD_LONG
        !newPassword.contains(Regex("^(?=.*[0-9])")) -> PASSWORD_REQUIRED_CHARS
        !newPassword.contains(Regex("^(?=.*[a-zA-Z])")) -> PASSWORD_REQUIRED_CHARS
        newPassword.any { listOf(' ', '\\', '`', '#').contains(it) } -> INVALID_CHARS_PASSWORD
        else -> null
    }
}