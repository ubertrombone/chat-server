package com.joshrose.models

import com.joshrose.util.Username
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

data class User(
    val id: Int,
    val password: String,
    val username: Username,
    val isOnline: Boolean,
    val lastOnline: Instant,
    val friendList: Set<Int>,
    val blockedList: Set<Int>,
    val status: String?,
    val cache: Boolean
)

object Users: Table() {
    val id = integer("id").autoIncrement()
    val password = varchar("password", 500)
    val username = varchar("username", 24)
    val isOnline = bool("isOnline")
    val lastOnline = timestamp("lastOnline")
    val friendList = varchar("friendList", 50000).nullable()
    val blockedList = varchar("blockedList", 50000).nullable()
    val status = varchar("status", 256).nullable()
    val cache = bool("cache")

    override val primaryKey = PrimaryKey(id)
}
