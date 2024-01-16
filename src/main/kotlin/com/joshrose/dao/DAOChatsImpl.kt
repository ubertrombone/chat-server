package com.joshrose.dao

import com.joshrose.dao.DatabaseFactory.dbQuery
import com.joshrose.models.Chat
import com.joshrose.models.Chats
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class DAOChatsImpl : DAOChats {
    private fun resultRowToChat(row: ResultRow) = Chat(id = row[Chats.id], with = row[Chats.with])

    override suspend fun allChats(): List<Chat> = dbQuery {
        Chats.selectAll().map(::resultRowToChat)
    }

    override suspend fun chatFromID(id: Int): Chat? = dbQuery {
        Chats.selectAll().where { Chats.id eq id }.map(::resultRowToChat).singleOrNull()
    }

    override suspend fun chatFromUsername(with: Int): Chat? = dbQuery {
        Chats.selectAll().where { Chats.with eq with }.map(::resultRowToChat).singleOrNull()
    }

    override suspend fun addChat(with: Int): Chat? = dbQuery {
        Chats.insert { it[this.with] = with }.resultedValues?.singleOrNull()?.let(::resultRowToChat)
    }

    override suspend fun editChat(chat: Chat): Boolean = dbQuery {
        Chats.update({ Chats.id eq chat.id }) { it[with] = chat.with } > 0
    }

    override suspend fun deleteChat(id: Int): Boolean = dbQuery {
        Chats.deleteWhere { this.id eq id } > 0
    }
}