package com.joshrose.status_page

import com.joshrose.Constants.GROUP_NAME_EXISTS
import com.joshrose.Constants.GROUP_NAME_INVALID_CHARS
import com.joshrose.Constants.GROUP_NAME_TOO_LONG
import com.joshrose.Constants.GROUP_NAME_TOO_SHORT
import com.joshrose.Constants.INCORRECT_CREDS
import com.joshrose.Constants.INVALID_CHARS_PASSWORD
import com.joshrose.Constants.INVALID_CHARS_USERNAME
import com.joshrose.Constants.PASSWORD_LONG
import com.joshrose.Constants.PASSWORD_REQUIRED_CHARS
import com.joshrose.Constants.PASSWORD_SHORT
import com.joshrose.Constants.STATUS_TOO_LONG
import com.joshrose.Constants.USERNAME_DOESNT_EXIST
import com.joshrose.Constants.USERNAME_EXISTS
import com.joshrose.Constants.USERNAME_TOO_LONG
import com.joshrose.Constants.USERNAME_TOO_SHORT
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.UnprocessableEntity
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

@Suppress("DuplicatedCode")
fun StatusPagesConfig.statusPages() {
    exception<RequestValidationException> { call, cause ->
        when {
            cause.reasons.any { it == USERNAME_EXISTS || it == GROUP_NAME_EXISTS } ->
                call.respond(Conflict, cause.reasons.joinToString())
            cause.reasons.any {
                it == PASSWORD_SHORT || it == USERNAME_TOO_LONG || it == INVALID_CHARS_USERNAME ||
                    it == PASSWORD_LONG || it == PASSWORD_REQUIRED_CHARS || it == INVALID_CHARS_PASSWORD ||
                    it == USERNAME_DOESNT_EXIST || it == STATUS_TOO_LONG || it == USERNAME_TOO_SHORT ||
                    it == GROUP_NAME_TOO_SHORT || it == GROUP_NAME_TOO_LONG || it == GROUP_NAME_INVALID_CHARS
            } -> call.respond(UnprocessableEntity, cause.reasons.joinToString())
            cause.reasons.contains(INCORRECT_CREDS) -> call.respond(BadRequest, cause.reasons.joinToString())
        }
    }
}