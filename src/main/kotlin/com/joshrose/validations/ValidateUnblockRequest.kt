package com.joshrose.validations

import com.joshrose.Constants.USER_NOT_BLOCKED
import com.joshrose.plugins.dao
import com.joshrose.requests.UnblockUserRequest
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.requestvalidation.ValidationResult.*

fun RequestValidationConfig.validateUnblockRequest() {
    validate<UnblockUserRequest> { request ->
        val blockList = dao.user(request.id)!!.blockedList?.split(";")
        blockList?.let {
            if (!it.contains(request.otherUser)) Invalid(USER_NOT_BLOCKED) else Valid
        } ?: Invalid(USER_NOT_BLOCKED)
    }
}