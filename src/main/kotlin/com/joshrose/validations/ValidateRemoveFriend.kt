package com.joshrose.validations

import com.joshrose.Constants.FRIEND_DOESNT_EXIST
import com.joshrose.plugins.dao
import com.joshrose.requests.RemoveFriendRequest
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.requestvalidation.ValidationResult.*

fun RequestValidationConfig.validateRemoveFriend() {
    validate<RemoveFriendRequest> { request ->
        val friendList = dao.user(request.id)!!.friendList?.split(";")
        friendList?.let { if (!it.contains(request.otherUser)) Invalid(FRIEND_DOESNT_EXIST) else Valid }
            ?: Invalid(FRIEND_DOESNT_EXIST)
    }
}