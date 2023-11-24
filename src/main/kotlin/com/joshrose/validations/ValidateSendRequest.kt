package com.joshrose.validations

import com.joshrose.Constants.FRIEND_ALREADY_ADDED
import com.joshrose.Constants.FRIEND_REQUEST_EXISTS
import com.joshrose.Constants.REQUEST_ALREADY_RECEIVED
import com.joshrose.Constants.USERNAME_DOESNT_EXIST
import com.joshrose.plugins.dao
import com.joshrose.plugins.friendRequestDao
import com.joshrose.requests.SendRequestRequest
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.requestvalidation.ValidationResult.*

fun RequestValidationConfig.validateSendRequest() {
    validate<SendRequestRequest> { request ->
        val toUser = dao.user(request.toId)
        val friendList = dao.user(request.requesterId)!!.friendList?.split(";")
        when {
            friendRequestDao.friendRequestExists(request.requesterId, request.toId) -> Invalid(FRIEND_REQUEST_EXISTS)
            toUser == null -> Invalid(USERNAME_DOESNT_EXIST)
            friendRequestDao.friendRequestExists(request.toId, request.requesterId) -> Invalid(REQUEST_ALREADY_RECEIVED)
            friendList == null -> Valid
            else -> if (friendList.contains(toUser.username)) Invalid(FRIEND_ALREADY_ADDED) else Valid
        }
    }
}