package com.joshrose.validations

import com.joshrose.Constants.INCORRECT_CREDS
import com.joshrose.plugins.dao
import com.joshrose.requests.AuthenticationRequest
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.requestvalidation.ValidationResult.*

fun RequestValidationConfig.validateAuth() {
    validate<AuthenticationRequest> { credentials ->
        if (!dao.checkPassword(credentials.username, credentials.password))
            Invalid(INCORRECT_CREDS)
        else Valid
    }
}