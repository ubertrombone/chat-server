@file:Suppress("DuplicatedCode")

package com.joshrose.dao

import com.joshrose.dao.DatabaseFactory.dbQuery
import com.joshrose.models.FriendInfo
import com.joshrose.models.User
import com.joshrose.models.Users
import com.joshrose.models.Users.username
import com.joshrose.security.checkHashForPassword
import com.joshrose.util.Username
import com.joshrose.util.toUsername
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class DAOUserImpl : DAOUser {
    private fun resultRowToUser(row: ResultRow) = User(
        //id = row[Users.id],
        password = row[Users.password],
        username = row[Users.username].toUsername(),
        isOnline = row[Users.isOnline],
        lastOnline = row[Users.lastOnline].toKotlinInstant(),
        friendList = row[Users.friendList]?.split(";")?.map { it.toUsername() }?.toSet() ?: emptySet(),
        blockedList = row[Users.blockedList]?.split(";")?.map { it.toUsername() }?.toSet() ?: emptySet(),
        status = row[Users.status]
    )
    override suspend fun allUsers(): List<User> = dbQuery {
        Users.selectAll().map(::resultRowToUser)
    }

    override suspend fun user(username: Username): User? = dbQuery {
        Users
            .select { Users.username.lowerCase() eq username.name.lowercase() }
            .map(::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun friends(username: Username): Set<FriendInfo> = dbQuery {
        Users
            .select { Users.username.lowerCase() eq username.name.lowercase() }
            .map(::resultRowToUser)
            .singleOrNull()!!
            .friendList
            .map {
                // TODO: Filter out names in the block list
                // TODO: with new account deletion setup, user could be null
                user(it)!!.let { user ->
                    FriendInfo(
                        username = user.username,
                        isOnline = user.isOnline,
                        lastOnline = if (!user.isOnline) user.lastOnline else null
                    )
                }
            }
            .toSet()
    }

    override suspend fun addNewUser(
        username: Username,
        password: String,
        isOnline: Boolean,
        lastOnline: Instant,
        friendList: Set<String>,
        blockedList: Set<String>,
        status: String?
    ): User? = dbQuery {
        val insertStatement = Users.insert {
            it[Users.username] = username.name
            it[Users.password] = password
            it[Users.isOnline] = isOnline
            it[Users.lastOnline] = lastOnline.toJavaInstant()
            it[Users.friendList] = friendList.joinToString(";")
            it[Users.blockedList] = blockedList.joinToString(";")
            it[Users.status] = status
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)
    }

    override suspend fun editUser(user: User): Boolean = dbQuery {
        Users.update({ username eq user.username.name }) {
            it[username] = user.username.name
            it[password] = user.password
            it[isOnline] = user.isOnline
            it[lastOnline] = user.lastOnline.toJavaInstant()
            it[friendList] = user.friendList.joinToString(";")
            it[blockedList] = user.blockedList.joinToString(";")
            it[status] = user.status
        } > 0
    }

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