package com.joshrose.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Chat(
    val id: Int,
    val userOne: Int,
    val userTwo: Int
)

object Chats: Table() {
    val id = integer("id").autoIncrement()
    val userOne = integer("userOne") references Users.id
    val userTwo = integer("userTwo") references Users.id

    override val primaryKey = PrimaryKey(id)
}
