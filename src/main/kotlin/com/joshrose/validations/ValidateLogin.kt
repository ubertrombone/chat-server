package com.joshrose.validations

import com.joshrose.Constants.INCORRECT_CREDS
import com.joshrose.plugins.dao
import com.joshrose.requests.LoginRequest
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.requestvalidation.ValidationResult.*

fun RequestValidationConfig.validateLogin() {
    validate<LoginRequest> { credentials ->
        if (!dao.checkPassword(credentials.username, credentials.password))
            Invalid(INCORRECT_CREDS)
        else Valid
    }
}