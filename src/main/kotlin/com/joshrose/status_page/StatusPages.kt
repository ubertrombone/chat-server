package com.joshrose.status_page

import com.joshrose.Constants.FRIEND_ALREADY_ADDED
import com.joshrose.Constants.FRIEND_DOESNT_EXIST
import com.joshrose.Constants.FRIEND_REQUEST_DOESNT_EXIST
import com.joshrose.Constants.FRIEND_REQUEST_EXISTS
import com.joshrose.Constants.GROUP_NAME_EXISTS
import com.joshrose.Constants.GROUP_NAME_INVALID_CHARS
import com.joshrose.Constants.GROUP_NAME_TOO_LONG
import com.joshrose.Constants.GROUP_NAME_TOO_SHORT
import com.joshrose.Constants.INCORRECT_CREDS
import com.joshrose.Constants.INCORRECT_PASSWORD
import com.joshrose.Constants.INVALID_CHARS_PASSWORD
import com.joshrose.Constants.INVALID_CHARS_USERNAME
import com.joshrose.Constants.PASSWORDS_DONT_MATCH
import com.joshrose.Constants.PASSWORD_LONG
import com.joshrose.Constants.PASSWORD_MUST_BE_NEW
import com.joshrose.Constants.PASSWORD_REQUIRED_CHARS
import com.joshrose.Constants.PASSWORD_SHORT
import com.joshrose.Constants.REQUEST_ALREADY_RECEIVED
import com.joshrose.Constants.STATUS_TOO_LONG
import com.joshrose.Constants.USERNAME_DOESNT_EXIST
import com.joshrose.Constants.USERNAME_EXISTS
import com.joshrose.Constants.USERNAME_TOO_LONG
import com.joshrose.Constants.USERNAME_TOO_SHORT
import com.joshrose.Constants.USER_ALREADY_BLOCKED
import com.joshrose.Constants.USER_NOT_BLOCKED
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.UnprocessableEntity
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun StatusPagesConfig.statusPages() {
    exception<RequestValidationException> { call, cause ->
        when {
            cause.reasons.any {
                it == USERNAME_EXISTS || it == PASSWORD_MUST_BE_NEW || it == FRIEND_ALREADY_ADDED ||
                    it == USER_ALREADY_BLOCKED || it == FRIEND_REQUEST_EXISTS || it == GROUP_NAME_EXISTS
            } ->
                call.respond(Conflict, cause.reasons.joinToString())

            cause.reasons.any {
                it == PASSWORD_SHORT || it == PASSWORDS_DONT_MATCH || it == USERNAME_TOO_LONG ||
                    it == INVALID_CHARS_USERNAME || it == PASSWORD_LONG || it == PASSWORD_REQUIRED_CHARS ||
                    it == INVALID_CHARS_PASSWORD || it == USERNAME_DOESNT_EXIST || it == STATUS_TOO_LONG ||
                    it == FRIEND_REQUEST_DOESNT_EXIST || it == REQUEST_ALREADY_RECEIVED || it == USERNAME_TOO_SHORT ||
                    it == GROUP_NAME_TOO_SHORT || it == GROUP_NAME_TOO_LONG || it == GROUP_NAME_INVALID_CHARS
            } ->
                call.respond(UnprocessableEntity, cause.reasons.joinToString())

            cause.reasons.any {
                it == INCORRECT_PASSWORD || it == INCORRECT_CREDS || it == FRIEND_DOESNT_EXIST || it == USER_NOT_BLOCKED
            } ->
                call.respond(BadRequest, cause.reasons.joinToString())
        }
    }
}