package com.joshrose.util

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
import com.joshrose.Constants.UNKNOWN_ERROR
import com.joshrose.Constants.USERNAME_DOESNT_EXIST
import com.joshrose.Constants.USERNAME_EXISTS
import com.joshrose.Constants.USERNAME_TOO_LONG
import com.joshrose.Constants.USERNAME_TOO_SHORT
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.UnprocessableEntity
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*

fun String.toUsername() = Username(name = this)
fun String.toUsernameOrNull() = runCatching { toUsername() }.getOrNull()

@Suppress("DuplicatedCode")
suspend inline fun <reified T: Any> ApplicationCall.receiveOrNull(): T? =
    runCatching { receive<T>() }.getOrElse { throwable ->
        if (throwable is RequestValidationException) {
            with(throwable.reasons) {
                when {
                    any { it == USERNAME_EXISTS || it == GROUP_NAME_EXISTS } -> respond(Conflict, joinToString())
                    any {
                        it == PASSWORD_SHORT || it == USERNAME_TOO_LONG || it == INVALID_CHARS_USERNAME ||
                            it == PASSWORD_LONG || it == PASSWORD_REQUIRED_CHARS || it == INVALID_CHARS_PASSWORD ||
                            it == USERNAME_DOESNT_EXIST || it == USERNAME_TOO_SHORT ||
                            it == GROUP_NAME_TOO_SHORT || it == GROUP_NAME_TOO_LONG || it == GROUP_NAME_INVALID_CHARS
                    } -> respond(UnprocessableEntity, joinToString())
                    contains(INCORRECT_CREDS) -> respond(BadRequest, joinToString())
                }
            }
        } else respond(BadRequest, UNKNOWN_ERROR)
        null
    }