package com.joshrose.dao

import com.joshrose.models.FriendInfo
import com.joshrose.models.User
import com.joshrose.util.Username
import java.time.LocalDateTime

interface DAOUser {
    suspend fun allUsers(): List<User>
    suspend fun user(username: Username): User?
    suspend fun friends(username: Username): Set<FriendInfo>
    suspend fun loginUser(username: Username): String
    suspend fun addNewUser(
        username: Username,
        password: String,
        isOnline: Boolean,
        lastOnline: LocalDateTime,
        friendList: Set<String>,
        blockedList: Set<String>,
        status: String?
    ): User?
    suspend fun editUser(user: User): Boolean
    suspend fun deleteUser(username: Username): Boolean
    suspend fun usernameExists(username: Username): Boolean
    suspend fun checkPassword(username: Username, passwordToCheck: String): Boolean
}