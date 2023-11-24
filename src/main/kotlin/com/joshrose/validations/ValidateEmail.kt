package com.joshrose.validations

import com.joshrose.Constants.EMAIL_EXISTS
import com.joshrose.Constants.INVALID_EMAIL
import com.joshrose.requests.AccountRequest
import com.joshrose.plugins.dao
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.requestvalidation.ValidationResult.*

fun RequestValidationConfig.validateEmail() {
    @Suppress("RegExpRedundantEscape")
    val regex =
        Regex("(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")

    validate<AccountRequest> { account ->
        when {
            !account.email.matches(regex) ->
                Invalid(INVALID_EMAIL)

            dao.emailExists(account.email) ->
                Invalid(EMAIL_EXISTS)

            else -> Valid
        }
    }
}