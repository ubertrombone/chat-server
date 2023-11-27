package com.joshrose.validations

import com.joshrose.Constants.INVALID_CHARS_USERNAME
import com.joshrose.Constants.REQUIREMENT_MAX
import com.joshrose.Constants.USERNAME_EXISTS
import com.joshrose.Constants.USERNAME_TOO_LONG
import com.joshrose.Constants.USERNAME_TOO_SHORT
import com.joshrose.plugins.dao
import com.joshrose.requests.AccountRequest
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.requestvalidation.ValidationResult.*

fun RequestValidationConfig.validateUsername() {
    validate<AccountRequest> { account ->
        when {
            account.username.name.isBlank() -> Invalid(USERNAME_TOO_SHORT)
            account.username.name.length > REQUIREMENT_MAX -> Invalid(USERNAME_TOO_LONG)
            account.username.name.any { !it.isLetterOrDigit() } -> Invalid(INVALID_CHARS_USERNAME)
            dao.usernameExists(account.username) -> Invalid(USERNAME_EXISTS)
            else -> Valid
        }
    }
}