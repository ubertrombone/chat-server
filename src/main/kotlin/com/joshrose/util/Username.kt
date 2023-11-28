package com.joshrose.util

import com.joshrose.Constants.REQUIREMENT_MAX
import com.joshrose.Constants.USERNAME_TOO_LONG
import com.joshrose.Constants.USERNAME_TOO_SHORT
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Username(val name: String) {
    init {
        require(name.isNotEmpty()) { USERNAME_TOO_SHORT }
        require(name.length <= REQUIREMENT_MAX) { "$USERNAME_TOO_LONG name: ${name.length}" }
    }
}