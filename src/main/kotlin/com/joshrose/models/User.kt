package com.joshrose.models

import com.joshrose.util.Username
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

data class User(
    val password: String,
    val username: Username,
    val isOnline: Boolean,
    val lastOnline: LocalDateTime,
    val friendList: Set<Username>,
    val blockedList: Set<Username>,
    val status: String?
)

object Users: Table() {
    val password = varchar("password", 500)
    val username = varchar("username", 24)
    val isOnline = bool("isOnline")
    val lastOnline = datetime("lastOnline")
    val friendList = varchar("friendList", 50000).nullable()
    val blockedList = varchar("blockedList", 50000).nullable()
    val status = varchar("status", 256).nullable()

    override val primaryKey = PrimaryKey(username)
}
