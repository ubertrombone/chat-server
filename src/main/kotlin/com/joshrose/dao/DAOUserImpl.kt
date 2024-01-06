@file:Suppress("DuplicatedCode")

package com.joshrose.dao

import com.joshrose.dao.DatabaseFactory.dbQuery
import com.joshrose.models.FriendInfo
import com.joshrose.models.User
import com.joshrose.models.Users
import com.joshrose.models.Users.id
import com.joshrose.models.Users.username
import com.joshrose.plugins.archiveDao
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
        id = row[id],
        password = row[Users.password],
        username = row[username].toUsername(),
        isOnline = row[Users.isOnline],
        lastOnline = row[Users.lastOnline].toKotlinInstant(),
        friendList = row[Users.friendList]
            ?.split(";")
            ?.mapNotNull { it.toIntOrNull() }
            ?.toSet()
            ?: emptySet(),
        blockedList = row[Users.blockedList]
            ?.split(";")
            ?.mapNotNull { it.toIntOrNull() }
            ?.toSet()
            ?: emptySet(),
        status = row[Users.status],
        cache = row[Users.cache]
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

    override suspend fun user(id: Int): User? = dbQuery {
        Users
            .select { Users.id eq id }
            .map(::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun userID(username: Username): Int? = dbQuery {
        Users
            .select { Users.username.lowerCase() eq username.name.lowercase() }
            .map(::resultRowToUser)
            .singleOrNull()?.id
    }

    override suspend fun getFriends(username: Username): Set<FriendInfo> = dbQuery {
        Users
            .select { Users.username.lowerCase() eq username.name.lowercase() }
            .map(::resultRowToUser)
            .singleOrNull()!!
            .friendList
            .onEach {
                if (archiveDao.userInArchive(user(it)!!.username))
                    with (user(username)!!) { editUser(copy(friendList = friendList.minus(it))) }
            }
            .mapNotNull {
                user(it)?.let { user ->
                    FriendInfo(
                        username = user.username,
                        isOnline = user.isOnline,
                        status = user.status,
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
        friendList: Set<Int>,
        blockedList: Set<Int>,
        status: String?,
        cache: Boolean
    ): User? = dbQuery {
        val insertStatement = Users.insert {
            it[Users.username] = username.name
            it[Users.password] = password
            it[Users.isOnline] = isOnline
            it[Users.lastOnline] = lastOnline.toJavaInstant()
            it[Users.friendList] = friendList.joinToString(";") { id -> id.toString() }
            it[Users.blockedList] = blockedList.joinToString(";") { id -> id.toString() }
            it[Users.status] = status
            it[Users.cache] = cache
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)
    }

    override suspend fun editUser(user: User): Boolean = dbQuery {
        Users.update({ id eq user.id }) {
            it[username] = user.username.name
            it[password] = user.password
            it[isOnline] = user.isOnline
            it[lastOnline] = user.lastOnline.toJavaInstant()
            it[friendList] = user.friendList.joinToString(";") { id -> id.toString() }
            it[blockedList] = user.blockedList.joinToString(";") { id -> id.toString() }
            it[status] = user.status
            it[cache] = user.cache
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