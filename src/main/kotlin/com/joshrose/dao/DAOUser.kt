package com.joshrose.dao

import com.joshrose.models.User
import java.time.LocalDateTime

interface DAOUser {
    suspend fun allUsers(): List<User>
    suspend fun user(id: Int): User?
    suspend fun loginUser(email: String): Int
    suspend fun addNewUser(
        email: String,
        password: String,
        username: String,
        isOnline: Boolean,
        lastOnline: LocalDateTime,
        friendList: String?,
        blockedList: String?,
        status: String?
    ): User?
    suspend fun editUser(user: User): Boolean
    suspend fun deleteUser(id: Int): Boolean
    suspend fun usernameExists(username: String): Boolean
    suspend fun emailExists(email: String): Boolean
    suspend fun checkPassword(email: String, passwordToCheck: String): Boolean
}