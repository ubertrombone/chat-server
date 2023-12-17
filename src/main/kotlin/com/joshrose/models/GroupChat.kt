package com.joshrose.models

import com.joshrose.util.Username
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

@Serializable
data class GroupChat(
    val name: String,
    val creator: Username,
    val createdDate: Instant,
    val members: Set<Username>
)

object GroupChats: Table() {
    val name = varchar("name", 100)
    val creator = varchar("creator", length = 24) references Users.username
    val createdDate = timestamp("createdDate")
    val members = varchar("members", 50000).nullable()

    override val primaryKey = PrimaryKey(name)
}