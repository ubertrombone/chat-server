package com.joshrose.validations

import com.joshrose.Constants.INVALID_CHARS_USERNAME
import com.joshrose.Constants.REQUIREMENT_MAX
import com.joshrose.Constants.USERNAME_EXISTS
import com.joshrose.Constants.USERNAME_TOO_LONG
import com.joshrose.plugins.dao
import com.joshrose.requests.UpdateUsernameRequest
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.requestvalidation.ValidationResult.*

fun RequestValidationConfig.validateUsernameUpdate() {
    validate<UpdateUsernameRequest> { username ->
        when {
            username.newUsername.length > REQUIREMENT_MAX -> Invalid(USERNAME_TOO_LONG)
            username.newUsername.any { !it.isLetterOrDigit() } -> Invalid(INVALID_CHARS_USERNAME)
            dao.usernameExists(username.newUsername) -> Invalid(USERNAME_EXISTS)
            else -> Valid
        }
    }
}