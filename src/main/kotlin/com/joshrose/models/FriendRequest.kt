package com.joshrose.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class FriendRequest(
    val id: Int,
    val requesterId: Int,
    val toId: Int
)

object FriendRequests: Table() {
    val id = integer("id").autoIncrement()
    val requesterId = integer("requesterId") references Users.id
    val toId = integer("toId") references Users.id

    override val primaryKey = PrimaryKey(id)
}
