package com.joshrose.validations

import com.joshrose.Constants
import com.joshrose.Constants.USER_ALREADY_BLOCKED
import com.joshrose.plugins.dao
import com.joshrose.requests.BlockUserRequest
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.requestvalidation.ValidationResult.*

fun RequestValidationConfig.validateBlockRequest() {
    validate<BlockUserRequest> { request ->
        if (!dao.usernameExists(request.otherUser)) Invalid(Constants.USERNAME_DOESNT_EXIST)
        else {
            val blockList = dao.user(request.id)!!.blockedList?.split(";")
            blockList?.let { if (it.contains(request.otherUser)) Invalid(USER_ALREADY_BLOCKED) else Valid } ?: Valid
        }
    }
}