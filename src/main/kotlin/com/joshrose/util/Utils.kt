package com.joshrose.util

import com.joshrose.Constants.GROUP_NAME_EXISTS
import com.joshrose.Constants.GROUP_NAME_INVALID_CHARS
import com.joshrose.Constants.GROUP_NAME_TOO_LONG
import com.joshrose.Constants.GROUP_NAME_TOO_SHORT
import com.joshrose.Constants.INCORRECT_CREDS
import com.joshrose.Constants.UNKNOWN_ERROR
import com.joshrose.Constants.USERNAME_DOESNT_EXIST
import com.joshrose.Constants.USERNAME_EXISTS
import com.joshrose.plugins.dao
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.UnprocessableEntity
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

fun String.toUsername() = Username(name = this)
fun String.toUsernameOrNull() = runCatching { toUsername() }.getOrNull()

// TODO: Get rid of GroupChat name checks too eventually
suspend inline fun <reified T: Any> ApplicationCall.receiveOrNull(): T? =
    runCatching { receive<T>() }.getOrElse { throwable ->
        if (throwable is RequestValidationException) {
            with(throwable.reasons) {
                when {
                    any { it == USERNAME_EXISTS || it == GROUP_NAME_EXISTS } -> respond(Conflict, joinToString())
                    any { it == USERNAME_DOESNT_EXIST || it == GROUP_NAME_TOO_SHORT ||
                            it == GROUP_NAME_TOO_LONG || it == GROUP_NAME_INVALID_CHARS
                    } -> respond(UnprocessableEntity, joinToString())
                    contains(INCORRECT_CREDS) -> respond(BadRequest, joinToString())
                }
            }
        } else respond(BadRequest, UNKNOWN_ERROR)
        null
    }

suspend fun ApplicationCall.addFriend(requesterUsername: Username, userUsername: Username, context: CoroutineContext) = withContext(context) {
    val requester = async {
        dao.user(requesterUsername)!!.let {
            dao.editUser(it.copy(friendList = it.friendList.plus(dao.userID(userUsername)!!)))
        }
    }

    val user = async {
        dao.user(userUsername)!!.let {
            dao.editUser(it.copy(friendList = it.friendList.plus(dao.userID(requesterUsername)!!)))
        }
    }

    if (awaitAll(requester, user).all { it })
        respond(OK, "${requesterUsername.name} & ${userUsername.name} are now friends!")
    else respond(BadRequest, UNKNOWN_ERROR)
}