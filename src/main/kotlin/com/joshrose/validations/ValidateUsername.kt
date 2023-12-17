package com.joshrose.validations

import com.joshrose.Constants.USERNAME_EXISTS
import com.joshrose.plugins.archiveDao
import com.joshrose.plugins.dao
import com.joshrose.requests.AccountRequest
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.requestvalidation.ValidationResult.*

fun RequestValidationConfig.validateUsername() {
    validate<AccountRequest> { account ->
        when {
            archiveDao.getArchivedUser(account.username) != null -> Invalid(USERNAME_EXISTS)
            dao.usernameExists(account.username) -> Invalid(USERNAME_EXISTS)
            else -> Valid
        }
    }
}