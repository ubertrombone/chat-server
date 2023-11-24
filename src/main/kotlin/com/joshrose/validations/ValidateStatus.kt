package com.joshrose.validations

import com.joshrose.Constants.MAX_STATUS_LENGTH
import com.joshrose.Constants.STATUS_TOO_LONG
import com.joshrose.requests.StatusRequest
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.requestvalidation.ValidationResult.*

fun RequestValidationConfig.validateStatus() {
    validate<StatusRequest> { request ->
        request.status?.let {
            if (it.length > MAX_STATUS_LENGTH) Invalid(STATUS_TOO_LONG) else Valid
        } ?: Valid
    }
}