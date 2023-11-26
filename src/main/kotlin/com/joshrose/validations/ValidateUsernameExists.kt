package com.joshrose.validations

import com.joshrose.Constants
import com.joshrose.plugins.dao
import com.joshrose.util.Username
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.requestvalidation.ValidationResult.*

fun RequestValidationConfig.validateUsernameExists() {
    validate<Username> { name ->
        if (!dao.usernameExists(name.name)) Invalid(Constants.USERNAME_DOESNT_EXIST) else Valid
    }
}