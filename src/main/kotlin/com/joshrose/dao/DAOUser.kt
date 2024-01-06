package com.joshrose.dao

import com.joshrose.models.FriendInfo
import com.joshrose.models.User
import com.joshrose.util.Username
import kotlinx.datetime.Instant

interface DAOUser {
    suspend fun allUsers(): List<User>
    suspend fun user(username: Username): User?
    suspend fun user(id: Int): User?
    suspend fun userID(username: Username): Int?
    suspend fun getFriends(username: Username): Set<FriendInfo>
    suspend fun addNewUser(
        username: Username,
        password: String,
        isOnline: Boolean,
        lastOnline: Instant,
        friendList: Set<Int>,
        blockedList: Set<Int>,
        status: String?,
        cache: Boolean
    ): User?
    suspend fun editUser(user: User): Boolean
    suspend fun deleteUser(username: Username): Boolean
    suspend fun usernameExists(username: Username): Boolean
    suspend fun checkPassword(username: Username, passwordToCheck: String): Boolean
}