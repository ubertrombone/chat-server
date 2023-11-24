package com.joshrose.validations

import com.joshrose.Constants.FRIEND_REQUEST_DOESNT_EXIST
import com.joshrose.plugins.friendRequestDao
import com.joshrose.requests.CancelFriendRequestRequest
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.requestvalidation.ValidationResult.*

fun RequestValidationConfig.validateCancelFriendRequest() {
    validate<CancelFriendRequestRequest> { request ->
        friendRequestDao.friendRequest(request.id)?.let { Valid } ?: Invalid(FRIEND_REQUEST_DOESNT_EXIST)
    }
}