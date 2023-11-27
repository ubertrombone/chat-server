@file:Suppress("DuplicatedCode")

package com.joshrose.dao

import com.joshrose.dao.DatabaseFactory.dbQuery
import com.joshrose.models.User
import com.joshrose.models.Users
import com.joshrose.models.Users.username
import com.joshrose.security.checkHashForPassword
import com.joshrose.util.Username
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

class DAOUserImpl : DAOUser {
    private fun resultRowToUser(row: ResultRow) = User(
        //id = row[Users.id],
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

//    override suspend fun user(id: Int): User? = dbQuery {
//        Users
//            .select { Users.id eq id }
//            .map(::resultRowToUser)
//            .singleOrNull()
//    }

    override suspend fun user(username: Username): User? = dbQuery {
        Users
            .select { Users.username.lowerCase() eq username.name.lowercase() }
            .map(::resultRowToUser)
            .singleOrNull()
    }

    // TODO: should probably remove now
    override suspend fun loginUser(username: Username): String = dbQuery {
        Users
            .select { Users.username.lowerCase() eq username.name.lowercase() }
            .map(::resultRowToUser)
            .single()
            .username
            //.id
    }

    override suspend fun addNewUser(
        username: Username,
        password: String,
        isOnline: Boolean,
        lastOnline: LocalDateTime,
        friendList: String?,
        blockedList: String?,
        status: String?
    ): User? = dbQuery {
        val insertStatement = Users.insert {
            it[Users.username] = username.name
            it[Users.password] = password
            it[Users.isOnline] = isOnline
            it[Users.lastOnline] = lastOnline
            it[Users.friendList] = friendList
            it[Users.blockedList] = blockedList
            it[Users.status] = status
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)
    }

    override suspend fun editUser(user: User): Boolean = dbQuery {
        Users.update({ username eq user.username }) {
            it[username] = user.username
            it[password] = user.password
            it[isOnline] = user.isOnline
            it[lastOnline] = user.lastOnline
            it[friendList] = user.friendList
            it[blockedList] = user.blockedList
            it[status] = user.status
        } > 0
    }

//    override suspend fun deleteUser(id: Int): Boolean = dbQuery {
//        Users.deleteWhere { Users.id eq id } > 0
//    }
    override suspend fun deleteUser(username: Username): Boolean = dbQuery {
        Users.deleteWhere { Users.username.lowerCase() eq username.name.lowercase() } > 0
    }

    override suspend fun usernameExists(username: Username): Boolean = dbQuery {
        Users.select { Users.username.lowerCase() eq username.name.lowercase() }.count().toInt() > 0
    }

    override suspend fun checkPassword(username: Username, passwordToCheck: String): Boolean = dbQuery {
        val actualPassword = Users
            .select { Users.username.lowerCase() eq username.name.lowercase() }
            .map(::resultRowToUser)
            .singleOrNull()?.password ?: return@dbQuery false

        return@dbQuery checkHashForPassword(passwordToCheck, actualPassword)
    }
}