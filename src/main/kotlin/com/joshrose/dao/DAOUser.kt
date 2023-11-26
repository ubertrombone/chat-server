package com.joshrose.dao

import com.joshrose.models.User
import java.time.LocalDateTime

interface DAOUser {
    suspend fun allUsers(): List<User>
    //suspend fun user(id: Int): User?
    suspend fun user(username: String): User?
    //suspend fun loginUser(username: String): Int
    suspend fun loginUser(username: String): String
    suspend fun addNewUser(
        username: String,
        password: String,
        isOnline: Boolean,
        lastOnline: LocalDateTime,
        friendList: String?,
        blockedList: String?,
        status: String?
    ): User?
    suspend fun editUser(user: User): Boolean
    //suspend fun deleteUser(id: Int): Boolean
    suspend fun deleteUser(username: String): Boolean
    suspend fun usernameExists(username: String): Boolean
    suspend fun checkPassword(username: String, passwordToCheck: String): Boolean
}