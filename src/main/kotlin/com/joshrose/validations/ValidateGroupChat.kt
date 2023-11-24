package com.joshrose.validations

import com.joshrose.Constants.GROUP_NAME_EXISTS
import com.joshrose.Constants.GROUP_NAME_INVALID_CHARS
import com.joshrose.Constants.GROUP_NAME_MAXIMUM
import com.joshrose.Constants.GROUP_NAME_TOO_LONG
import com.joshrose.Constants.GROUP_NAME_TOO_SHORT
import com.joshrose.plugins.groupChatDao
import com.joshrose.requests.GroupChatNameRequest
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.requestvalidation.ValidationResult.*

fun RequestValidationConfig.validateGroupChat() {
    validate<GroupChatNameRequest> { request ->
        when {
            groupChatDao.groupChatNameExists(request.name) -> Invalid(GROUP_NAME_EXISTS)
            request.name.isBlank() -> Invalid(GROUP_NAME_TOO_SHORT)
            request.name.length > GROUP_NAME_MAXIMUM -> Invalid(GROUP_NAME_TOO_LONG)
            request.name.any { !it.isLetterOrDigit() } -> Invalid(GROUP_NAME_INVALID_CHARS)
            else -> Valid
        }
    }
}