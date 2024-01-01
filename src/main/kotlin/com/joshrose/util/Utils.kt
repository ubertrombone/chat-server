package com.joshrose.util

import io.ktor.server.application.*
import io.ktor.server.request.*

fun String.toUsername() = Username(name = this)
fun String.toUsernameOrNull() = runCatching { toUsername() }.getOrNull()

suspend inline fun <reified T: Any> ApplicationCall.receiveOrNull(): T? =
    runCatching { receive<T>() }.getOrNull()