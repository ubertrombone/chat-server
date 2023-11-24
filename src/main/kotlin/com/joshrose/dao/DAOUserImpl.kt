@file:Suppress("DuplicatedCode")

package com.joshrose.dao

import com.joshrose.dao.DatabaseFactory.dbQuery
import com.joshrose.models.User
import com.joshrose.models.Users
import com.joshrose.models.Users.email
import com.joshrose.security.checkHashForPassword
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

class DAOUserImpl : DAOUser {
    private fun resultRowToUser(row: ResultRow) = User(
        id = row[Users.id],
        email = row[email],
        password = row[Users.password],
        username = row[Users.username],
        isOnline = row[Users.isOnline],
        lastOnline = row[Users.lastOnline],
        friendList = row[Users.friendList],
        blockedList = row[Users.blockedList],
        status = row[Users.status]
    )
    override suspend fun allUsers(): List<User> = dbQuery {
        Users.selectAll().map(::resultRowToUser)
    }

    override suspend fun user(id: Int): User? = dbQuery {
        Users
            .select { Users.id eq id }
            .map(::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun loginUser(email: String): Int = dbQuery {
        Users
            .select { Users.email eq email }
            .map(::resultRowToUser)
            .single()
            .id
    }

    override suspend fun addNewUser(
        email: String,
        password: String,
        username: String,
        isOnline: Boolean,
        lastOnline: LocalDateTime,
        friendList: String?,
        blockedList: String?,
        status: String?
    ): User? = dbQuery {
        val insertStatement = Users.insert {
            it[Users.email] = email
            it[Users.password] = password
            it[Users.username] = username
            it[Users.isOnline] = isOnline
            it[Users.lastOnline] = lastOnline
            it[Users.friendList] = friendList
            it[Users.blockedList] = blockedList
            it[Users.status] = status
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)
    }

    override suspend fun editUser(user: User): Boolean = dbQuery {
        Users.update({ email eq user.email }) {
            it[email] = user.email
            it[password] = user.password
            it[username] = user.username
            it[isOnline] = user.isOnline
            it[lastOnline] = user.lastOnline
            it[friendList] = user.friendList
            it[blockedList] = user.blockedList
            it[status] = user.status
        } > 0
    }

    override suspend fun deleteUser(id: Int): Boolean = dbQuery {
        Users.deleteWhere { Users.id eq id } > 0
    }

    override suspend fun usernameExists(username: String): Boolean = dbQuery {
        Users.select { Users.username eq username }.count().toInt() > 0
    }

    override suspend fun emailExists(email: String): Boolean = dbQuery {
        Users.select { Users.email eq email }.count().toInt() > 0
    }

    override suspend fun checkPassword(email: String, passwordToCheck: String): Boolean = dbQuery {
        val actualPassword = Users
            .select { Users.email eq email }
            .map(::resultRowToUser)
            .singleOrNull()?.password ?: return@dbQuery false

        return@dbQuery checkHashForPassword(passwordToCheck, actualPassword)
    }
}