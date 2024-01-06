package com.joshrose.dao

import com.joshrose.dao.DatabaseFactory.dbQuery
import com.joshrose.models.Archive
import com.joshrose.models.Archives
import com.joshrose.models.User
import com.joshrose.util.Username
import com.joshrose.util.toUsername
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import org.jetbrains.exposed.sql.*

class DAOArchiveImpl : DAOArchive {
    private fun resultRowToUser(row: ResultRow) = Archive(
        id = row[Archives.id],
        username = row[Archives.username].toUsername(),
        lastOnline = row[Archives.lastOnline].toKotlinInstant(),
        status = row[Archives.status]
    )

    override suspend fun allArchivedUsers(): Set<Archive> = dbQuery {
        Archives.selectAll().map(::resultRowToUser).toSet()
    }

    override suspend fun getArchivedUser(username: Username): Archive? = dbQuery {
        Archives
            .select { Archives.username.lowerCase() eq username.name.lowercase() }
            .map(::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun userInArchive(username: Username): Boolean = dbQuery {
        Archives
            .select { Archives.username.lowerCase() eq username.name.lowercase() }
            .map(::resultRowToUser)
            .singleOrNull()?.let { true } ?: false
    }

    override suspend fun addToArchives(user: User): Archive? = dbQuery {
        val insertStatement = Archives.insert {
            it[username] = user.username.name
            it[lastOnline] = user.lastOnline.toJavaInstant()
            it[status] = user.status
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)
    }
}