package com.joshrose.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

@Serializable
data class GroupChat(
    val id: Int,
    val name: String,
    val creator: Int,
    val createdDate: Instant,
    val members: Set<Int>
)

object GroupChats: Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    val creator = integer("creator") references Users.id
    val createdDate = timestamp("createdDate")
    val members = varchar("members", 50000).nullable()

    override val primaryKey = PrimaryKey(id)
}