package com.joshrose.models

import com.joshrose.util.Username
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class FriendRequest(
    val id: Int,
    val requesterUsername: Username,
    val toUsername: Username
)

object FriendRequests: Table() {
    val id = integer("id").autoIncrement()
    val requesterUsername = varchar("requesterId", length = 24) references Users.username
    val toUsername = varchar("toId", length = 24) references Users.username

    override val primaryKey = PrimaryKey(id)
}
