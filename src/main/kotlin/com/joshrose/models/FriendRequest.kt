package com.joshrose.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class FriendRequest(
    val id: Int,
    val requesterUsername: String,
    val toUsername: String
)

object FriendRequests: Table() {
    val id = integer("id").autoIncrement()
    val requesterUsername = varchar("requesterId", length = 24) references Users.username
    val toUsername = varchar("toId", length = 24) references Users.username

    override val primaryKey = PrimaryKey(id)
}
