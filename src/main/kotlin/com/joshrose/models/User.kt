package com.joshrose.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

data class User(
    val password: String,
    val username: String,
    val isOnline: Boolean,
    val lastOnline: LocalDateTime,
    val friendList: Set<String>,
    val blockedList: Set<String>,
    val status: String?
)

object Users: Table() {
    val password = varchar("password", 500)
    val username = varchar("username", 24)
    val isOnline = bool("isOnline")
    val lastOnline = datetime("lastOnline")
    val friendList = varchar("friendList", 50000)
    val blockedList = varchar("blockedList", 50000)
    val status = varchar("status", 256).nullable()

    override val primaryKey = PrimaryKey(username)
}
