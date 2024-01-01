package com.joshrose.util

fun String.toUsername() = Username(name = this)
fun String?.toUsernameOrNull() = this?.let { Username(this) }