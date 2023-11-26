package com.joshrose.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class FriendRequest(
    val id: Int,
    val requesterId: String,
    val toId: String
)

object FriendRequests: Table() {
    val id = integer("id").autoIncrement()
    val requesterId = varchar("requesterId", length = 24) references Users.username
    val toId = varchar("toId", length = 24) references Users.username

    override val primaryKey = PrimaryKey(id)
}
