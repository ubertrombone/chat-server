package com.joshrose.models

import com.joshrose.util.Username
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

data class Archive(
    val username: Username,
    val lastOnline: Instant,
    val status: String?
)

object Archives : Table() {
    val username = varchar("username", 24)
    val lastOnline = timestamp("lastOnline")
    val status = varchar("status", 256).nullable()

    override val primaryKey = PrimaryKey(Users.username)
}

