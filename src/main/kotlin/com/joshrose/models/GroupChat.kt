package com.joshrose.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

data class GroupChat(
    val id: Int,
    val name: String,
    val creator: String,
    val createdDate: LocalDateTime,
    val members: String?
)

object GroupChats: Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    val creator = varchar("creator", length = 24) references Users.username
    val createdDate = datetime("createdDate")
    val members = varchar("members", 50000).nullable()

    override val primaryKey = PrimaryKey(id)
}