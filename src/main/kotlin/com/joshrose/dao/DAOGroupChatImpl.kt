package com.joshrose.dao

import com.joshrose.dao.DatabaseFactory.dbQuery
import com.joshrose.models.GroupChat
import com.joshrose.models.GroupChats
import com.joshrose.util.Username
import com.joshrose.util.toUsername
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class DAOGroupChatImpl : DAOGroupChat {
    private fun resultRowToChatGroup(row: ResultRow) = GroupChat(
        name = row[GroupChats.name],
        creator = row[GroupChats.creator].toUsername(),
        createdDate = row[GroupChats.createdDate].toKotlinInstant(),
        members = row[GroupChats.members]
            ?.split(";")
            ?.mapNotNull { try { it.toUsername() } catch (e: IllegalArgumentException) { null } }
            ?.toSet()
            ?: emptySet(),
    )
    override suspend fun allGroupChats(): Set<GroupChat> = dbQuery {
        GroupChats.selectAll().map(::resultRowToChatGroup).toSet()
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
        createdDate: Instant,
        members: Set<String>
    ): GroupChat? = dbQuery {
        val insertStatement = GroupChats.insert {
            it[GroupChats.name] = name
            it[GroupChats.creator] = creator.name
            it[GroupChats.createdDate] = createdDate.toJavaInstant()
            it[GroupChats.members] = members.joinToString(";")
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToChatGroup)
    }

    override suspend fun editGroupChat(groupChat: GroupChat): Boolean = dbQuery {
        GroupChats.update({ GroupChats.name eq groupChat.name }) {
            it[name] = groupChat.name
            it[creator] = groupChat.creator.name
            it[createdDate] = groupChat.createdDate.toJavaInstant()
            it[members] = groupChat.members.joinToString(";")
        } > 0
    }

    override suspend fun deleteGroupChat(name: String): Boolean = dbQuery {
        GroupChats.deleteWhere { GroupChats.name eq name } > 0
    }
}