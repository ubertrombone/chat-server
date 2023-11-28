package com.joshrose.util

import com.joshrose.Constants.INVALID_CHARS_USERNAME
import com.joshrose.Constants.REQUIREMENT_MAX
import com.joshrose.Constants.USERNAME_TOO_LONG
import com.joshrose.Constants.USERNAME_TOO_SHORT
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Username(val name: String) {
    init {
        require(name.isNotEmpty()) { USERNAME_TOO_SHORT }
        require(name.length <= REQUIREMENT_MAX) { "$USERNAME_TOO_LONG Length is: ${name.length}" }
        require(name.none { !it.isLetterOrDigit() }) { INVALID_CHARS_USERNAME }
    }
}