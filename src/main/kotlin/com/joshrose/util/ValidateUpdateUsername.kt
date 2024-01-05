package com.joshrose.util

import com.joshrose.Constants.USERNAME_EXISTS
import com.joshrose.plugins.archiveDao
import com.joshrose.plugins.dao
import com.joshrose.requests.UpdateUsernameRequest

suspend fun validateUpdateNewUsername(request: UpdateUsernameRequest) = with (request) {
    when {
        archiveDao.getArchivedUser(newUsername) != null -> USERNAME_EXISTS
        dao.usernameExists(newUsername) -> USERNAME_EXISTS
        else -> null
    }
}