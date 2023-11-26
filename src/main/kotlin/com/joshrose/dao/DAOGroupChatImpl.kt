package com.joshrose.dao

import com.joshrose.dao.DatabaseFactory.dbQuery
import com.joshrose.models.GroupChat
import com.joshrose.models.GroupChats
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

class DAOGroupChatImpl : DAOGroupChat {
    private fun resultRowToChatGroup(row: ResultRow) = GroupChat(
        id = row[GroupChats.id],
        name = row[GroupChats.name],
        creator = row[GroupChats.creator],
        createdDate = row[GroupChats.createdDate],
        population = row[GroupChats.population],
    )
    override suspend fun allGroupChats(): List<GroupChat> = dbQuery {
        GroupChats.selectAll().map(::resultRowToChatGroup)
    }

    override suspend fun groupChat(id: Int): GroupChat? = dbQuery {
        GroupChats
            .select { GroupChats.id eq id }
            .map(::resultRowToChatGroup)
            .singleOrNull()
    }

    override suspend fun groupChatNameExists(name: String): Boolean = dbQuery {
        GroupChats.select { GroupChats.name eq name }.count().toInt() > 0
    }

    override suspend fun addNewGroupChat(
        name: String,
        creator: String,
        createdDate: LocalDateTime,
        population: Int
    ): GroupChat? = dbQuery {
        val insertStatement = GroupChats.insert {
            it[GroupChats.name] = name
            it[GroupChats.creator] = creator
            it[GroupChats.createdDate] = createdDate
            it[GroupChats.population] = population
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToChatGroup)
    }

    override suspend fun editGroupChat(groupChat: GroupChat): Boolean = dbQuery {
        GroupChats.update({ GroupChats.id eq groupChat.id }) {
            it[name] = groupChat.name
            it[creator] = groupChat.creator
            it[createdDate] = groupChat.createdDate
            it[population] = groupChat.population
        } > 0
    }

    override suspend fun deleteGroupChat(id: Int): Boolean = dbQuery {
        GroupChats.deleteWhere { GroupChats.id eq id } > 0
    }
}