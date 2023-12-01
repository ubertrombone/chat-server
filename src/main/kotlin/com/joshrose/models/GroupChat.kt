package com.joshrose.models

import com.joshrose.util.Username
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

data class GroupChat(
    val name: String,
    val creator: Username,
    val createdDate: LocalDateTime,
    val members: Set<Username>
)

object GroupChats: Table() {
    val name = varchar("name", 100)
    val creator = varchar("creator", length = 24) references Users.username
    val createdDate = datetime("createdDate")
    val members = varchar("members", 50000)

    override val primaryKey = PrimaryKey(name)
}