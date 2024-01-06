@file:Suppress("DuplicatedCode")

package com.joshrose.dao

import com.joshrose.dao.DatabaseFactory.dbQuery
import com.joshrose.models.FriendInfo
import com.joshrose.models.User
import com.joshrose.models.Users
import com.joshrose.models.Users.username
import com.joshrose.plugins.archiveDao
import com.joshrose.security.checkHashForPassword
import com.joshrose.util.Username
import com.joshrose.util.toUsername
import com.joshrose.util.toUsernameOrNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.datetime.Clock.System
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class DAOUserImpl : DAOUser {
    private fun resultRowToUser(row: ResultRow) = User(
        password = row[Users.password],
        username = row[username].toUsername(),
        isOnline = row[Users.isOnline],
        lastOnline = row[Users.lastOnline].toKotlinInstant(),
        friendList = row[Users.friendList]
            ?.split(";")
            ?.mapNotNull { it.toUsernameOrNull() }
            ?.toSet()
            ?: emptySet(),
        blockedList = row[Users.blockedList]
            ?.split(";")
            ?.mapNotNull { it.toUsernameOrNull() }
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

    override suspend fun friends(username: Username): Set<FriendInfo> = dbQuery {
        Users
            .select { Users.username.lowerCase() eq username.name.lowercase() }
            .map(::resultRowToUser)
            .singleOrNull()!!
            .friendList
            .onEach {
                if (archiveDao.userInArchive(it))
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
        friendList: Set<Username>,
        blockedList: Set<Username>,
        status: String?,
        cache: Boolean
    ): User? = dbQuery {
        val insertStatement = Users.insert {
            it[Users.username] = username.name
            it[Users.password] = password
            it[Users.isOnline] = isOnline
            it[Users.lastOnline] = lastOnline.toJavaInstant()
            it[Users.friendList] = friendList.joinToString(";") { name -> name.name }
            it[Users.blockedList] = blockedList.joinToString(";") { name -> name.name }
            it[Users.status] = status
            it[Users.cache] = cache
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)
    }

    override suspend fun editUser(user: User): Boolean = dbQuery {
        Users.update({ username eq user.username.name }) {
            it[username] = user.username.name
            it[password] = user.password
            it[isOnline] = user.isOnline
            it[lastOnline] = user.lastOnline.toJavaInstant()
            it[friendList] = user.friendList.joinToString(";") { name -> name.name }
            it[blockedList] = user.blockedList.joinToString(";") { name -> name.name }
            it[status] = user.status
            it[cache] = user.cache
        } > 0
    }

    override suspend fun updateUsername(oldUsername: Username, newUsername: Username): Boolean = dbQuery {
        with (user(oldUsername)!!) {
            Users.update({ Users.username eq username.name }) {
                it[username] = newUsername.name
                it[password] = this@with.password
                it[isOnline] = this@with.isOnline
                it[lastOnline] = System.now().toJavaInstant()
                it[friendList] = this@with.friendList.joinToString(";") { name -> name.name }
                it[blockedList] = this@with.blockedList.joinToString(";") { name -> name.name }
                it[status] = this@with.status
                it[cache] = this@with.cache
            } > 0
        }
    }

    override suspend fun updateFriendsList(oldUsername: Username, newUsername: Username): Boolean = dbQuery {
        CoroutineScope(Dispatchers.Default).async {
            user(newUsername)!!.friendList
                .map { async { user(it)!! } }.awaitAll()
                .map {
                    async {
                        editUser(user = it.copy(friendList = it.friendList.minus(oldUsername).plus(newUsername)))
                    }
                }.awaitAll()
        }.await().all { it }
    }

    override suspend fun updateBlockedLists(oldUsername: Username, newUsername: Username): Boolean = dbQuery {
        CoroutineScope(Dispatchers.Default).async {
            allUsers()
                .map {
                    async {
                        if (it.blockedList.contains(oldUsername))
                            editUser(user = it.copy(blockedList = it.blockedList.plus(newUsername).minus(oldUsername)))
                        else true
                    }
                }.awaitAll()
        }.await().all { it }
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