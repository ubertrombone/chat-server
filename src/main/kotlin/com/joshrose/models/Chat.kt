package com.joshrose.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Chat(val id: Int, val with: Int)

object Chats: Table() {
    val id = integer("id").autoIncrement()
    val with = integer("with") references Users.id

    override val primaryKey = PrimaryKey(id)
}
