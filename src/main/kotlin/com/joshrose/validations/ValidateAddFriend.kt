package com.joshrose.validations

import com.joshrose.Constants.FRIEND_ALREADY_ADDED
import com.joshrose.Constants.USERNAME_DOESNT_EXIST
import com.joshrose.plugins.dao
import com.joshrose.requests.AddFriendRequest
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.requestvalidation.ValidationResult.*

fun RequestValidationConfig.validateAddFriend() {
    validate<AddFriendRequest> { request ->
        if (!dao.usernameExists(request.otherUser)) Invalid(USERNAME_DOESNT_EXIST)
        else {
            val friendList = dao.user(request.id)!!.friendList?.split(";")
            friendList?.let { if (it.contains(request.otherUser)) Invalid(FRIEND_ALREADY_ADDED) else Valid } ?: Valid
        }
    }
}