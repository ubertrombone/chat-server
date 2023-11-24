package com.joshrose.validations

import com.joshrose.Constants.INVALID_CHARS_PASSWORD
import com.joshrose.Constants.PASSWORD_LONG
import com.joshrose.Constants.PASSWORD_REQUIRED_CHARS
import com.joshrose.Constants.PASSWORD_REQUIREMENT_MIN
import com.joshrose.Constants.PASSWORD_SHORT
import com.joshrose.Constants.REQUIREMENT_MAX
import com.joshrose.requests.AccountRequest
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.requestvalidation.ValidationResult.*

fun RequestValidationConfig.validatePassword() {
    val invalidChars = listOf(' ', '\\', '`', '#')
    validate<AccountRequest> { account ->
        when {
            !account.password.contains(Regex("^(?=.*[0-9])")) -> Invalid(PASSWORD_REQUIRED_CHARS)
            !account.password.contains(Regex("^(?=.*[a-zA-Z])")) -> Invalid(PASSWORD_REQUIRED_CHARS)
            account.password.any { invalidChars.contains(it) } -> Invalid(INVALID_CHARS_PASSWORD)
            account.password.length < PASSWORD_REQUIREMENT_MIN -> Invalid(PASSWORD_SHORT)
            account.password.length > REQUIREMENT_MAX -> Invalid(PASSWORD_LONG)
            else -> Valid
        }
    }
}