package com.joshrose.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

data class User(
    val id: Int,
    val password: String,
    val username: String,
    val isOnline: Boolean,
    val lastOnline: LocalDateTime,
    val friendList: String?,
    val blockedList: String?,
    val status: String?
)

object Users: Table() {
    val id = integer("id").autoIncrement()
    val password = varchar("password", 500)
    val username = varchar("username", 24)
    val isOnline = bool("isOnline")
    val lastOnline = datetime("lastOnline")
    val friendList = varchar("friendList", 5000).nullable()
    val blockedList = varchar("blockedList", 50000).nullable()
    val status = varchar("status", 256).nullable()

    override val primaryKey = PrimaryKey(id)
}
