package com.joshrose.dao

import com.joshrose.models.Archive
import com.joshrose.models.User
import com.joshrose.util.Username

interface DAOArchive {
    suspend fun allArchivedUsers(): Set<Archive>
    suspend fun getArchivedUser(username: Username): Archive?
    suspend fun userInArchive(username: Username): Boolean
    suspend fun addToArchives(user: User): Archive?
}