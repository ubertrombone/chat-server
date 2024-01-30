package com.joshrose.dao

import com.joshrose.dao.DatabaseFactory.dbQuery
import com.joshrose.models.Chat
import com.joshrose.models.Chats
import com.joshrose.plugins.cacheDao
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

// TODO: Chats should be deleted when an account is deleted; consider other reasons
//  2. When two users unfriend each other
//  3. When a user blocks another
class DAOChatsImpl : DAOChats {
    private fun resultRowToChat(row: ResultRow) = Chat(
        id = row[Chats.id],
        userOne = row[Chats.userOne],
        userTwo = row[Chats.userTwo],
    )

    override suspend fun allChats(): List<Chat> = dbQuery {
        Chats.selectAll().map(::resultRowToChat)
    }

    override suspend fun chatFromID(id: Int): Chat? = dbQuery {
        Chats.selectAll().where { Chats.id eq id }.map(::resultRowToChat).singleOrNull()
    }

    override suspend fun chat(userOne: Int, userTwo: Int): Chat? = dbQuery {
        Chats.selectAll()
            .where {
                ((Chats.userOne eq userOne) and (Chats.userTwo eq userTwo)) or ((Chats.userOne eq userTwo) and (Chats.userTwo eq userOne))
            }
            .map(::resultRowToChat)
            .singleOrNull()
    }

    override suspend fun addChat(userOne: Int, userTwo: Int): Chat? = dbQuery {
        Chats.insert {
            it[this.userOne] = userOne
            it[this.userTwo] = userTwo
        }.resultedValues?.singleOrNull()?.let(::resultRowToChat)
    }

    override suspend fun editChat(chat: Chat): Boolean = dbQuery {
        Chats.update({ Chats.id eq chat.id }) {
            it[userOne] = chat.userOne
            it[userTwo] = chat.userTwo
        } > 0
    }

    override suspend fun deleteChat(id: Int): Boolean = dbQuery {
        Chats.deleteWhere { this.id eq id } > 0 && cacheDao.deleteCacheOf(id)
    }
}