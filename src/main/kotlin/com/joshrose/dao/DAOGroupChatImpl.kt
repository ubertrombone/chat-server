package com.joshrose.dao

import com.joshrose.dao.DatabaseFactory.dbQuery
import com.joshrose.models.GroupChat
import com.joshrose.models.GroupChats
import com.joshrose.util.Username
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

class DAOGroupChatImpl : DAOGroupChat {
    private fun resultRowToChatGroup(row: ResultRow) = GroupChat(
        name = row[GroupChats.name],
        creator = row[GroupChats.creator],
        createdDate = row[GroupChats.createdDate],
        members = row[GroupChats.members],
    )
    override suspend fun allGroupChats(): List<GroupChat> = dbQuery {
        GroupChats.selectAll().map(::resultRowToChatGroup)
    }

    override suspend fun groupChat(name: String): GroupChat? = dbQuery {
        GroupChats
            .select { GroupChats.name eq name }
            .map(::resultRowToChatGroup)
            .singleOrNull()
    }

    override suspend fun groupChatNameExists(name: String): Boolean = dbQuery {
        GroupChats.select { GroupChats.name eq name }.count().toInt() > 0
    }

    override suspend fun addNewGroupChat(
        name: String,
        creator: Username,
        createdDate: LocalDateTime,
        members: String?
    ): GroupChat? = dbQuery {
        val insertStatement = GroupChats.insert {
            it[GroupChats.name] = name
            it[GroupChats.creator] = creator.name
            it[GroupChats.createdDate] = createdDate
            it[GroupChats.members] = members
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToChatGroup)
    }

    override suspend fun editGroupChat(groupChat: GroupChat): Boolean = dbQuery {
        GroupChats.update({ GroupChats.name eq groupChat.name }) {
            it[name] = groupChat.name
            it[creator] = groupChat.creator
            it[createdDate] = groupChat.createdDate
            it[members] = groupChat.members
        } > 0
    }

    override suspend fun deleteGroupChat(name: String): Boolean = dbQuery {
        GroupChats.deleteWhere { GroupChats.name eq name } > 0
    }
}