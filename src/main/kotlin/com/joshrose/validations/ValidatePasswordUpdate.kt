package com.joshrose.validations

import com.joshrose.Constants.INCORRECT_PASSWORD
import com.joshrose.Constants.INVALID_CHARS_PASSWORD
import com.joshrose.Constants.PASSWORDS_DONT_MATCH
import com.joshrose.Constants.PASSWORD_LONG
import com.joshrose.Constants.PASSWORD_MUST_BE_NEW
import com.joshrose.Constants.PASSWORD_REQUIRED_CHARS
import com.joshrose.Constants.PASSWORD_REQUIREMENT_MIN
import com.joshrose.Constants.PASSWORD_SHORT
import com.joshrose.Constants.REQUIREMENT_MAX
import com.joshrose.plugins.dao
import com.joshrose.requests.UpdatePasswordRequest
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.requestvalidation.ValidationResult.*

fun RequestValidationConfig.validatePasswordUpdate() {
    val invalidChars = listOf(' ', '\\', '`', '#')
    validate<UpdatePasswordRequest> { passwordRequest ->
        val email = dao.user(passwordRequest.id)!!.email
        when {
            !dao.checkPassword(email, passwordRequest.oldPassword) ->
                Invalid(INCORRECT_PASSWORD)

            passwordRequest.newPassword != passwordRequest.newPasswordConfirm ->
                Invalid(PASSWORDS_DONT_MATCH)

            passwordRequest.newPassword == passwordRequest.oldPassword ->
                Invalid(PASSWORD_MUST_BE_NEW)

            passwordRequest.newPassword.length < PASSWORD_REQUIREMENT_MIN ->
                Invalid(PASSWORD_SHORT)

            passwordRequest.newPassword.length > REQUIREMENT_MAX ->
                Invalid(PASSWORD_LONG)

            !passwordRequest.newPassword.contains(Regex("^(?=.*[0-9])")) ->
                Invalid(PASSWORD_REQUIRED_CHARS)

            !passwordRequest.newPassword.contains(Regex("^(?=.*[a-zA-Z])")) ->
                Invalid(PASSWORD_REQUIRED_CHARS)

            passwordRequest.newPassword.any { invalidChars.contains(it) } ->
                Invalid(INVALID_CHARS_PASSWORD)

            else -> Valid
        }
    }
}