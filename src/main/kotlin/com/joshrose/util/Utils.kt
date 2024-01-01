package com.joshrose.util

import io.ktor.server.application.*
import io.ktor.server.request.*

fun String.toUsername() = Username(name = this)
fun String.toUsernameOrNull() =
    try { Username(this) } catch (e: IllegalArgumentException) { null }

suspend inline fun <reified T: Any> ApplicationCall.receiveOrNull(): T? =
    try { receive<T>() } catch (e: ContentTransformationException) { null }