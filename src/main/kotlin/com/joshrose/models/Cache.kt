package com.joshrose.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

@Serializable
data class Cache(
    val message: String,
    val sender: Int,
    val timestamp: Instant,
    val chat: Int
)

object Caches: Table() {
    val id = integer("id").autoIncrement()
    val message = text(name = "message", eagerLoading = true)
    val sender = integer("sender") references Users.id
    val timestamp = timestamp("timestamp")
    val chat = integer("chat") references Chats.id

    override val primaryKey = PrimaryKey(id)
}
