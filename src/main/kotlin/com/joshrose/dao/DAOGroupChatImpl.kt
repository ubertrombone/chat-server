package com.joshrose.dao

import com.joshrose.dao.DatabaseFactory.dbQuery
import com.joshrose.models.GroupChat
import com.joshrose.models.GroupChats
import com.joshrose.util.Username
import com.joshrose.util.toUsername
import com.joshrose.util.toUsernameOrNull
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
            ?.mapNotNull { it.toUsernameOrNull() }
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
        members: Set<Username>
    ): GroupChat? = dbQuery {
        val insertStatement = GroupChats.insert {
            it[GroupChats.name] = name
            it[GroupChats.creator] = creator.name
            it[GroupChats.createdDate] = createdDate.toJavaInstant()
            it[GroupChats.members] = members.joinToString(";") { name -> name.name }
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToChatGroup)
    }

    override suspend fun editGroupChat(groupChat: GroupChat): Boolean = dbQuery {
        GroupChats.update({ GroupChats.name eq groupChat.name }) {
            it[name] = groupChat.name
            it[creator] = groupChat.creator.name
            it[createdDate] = groupChat.createdDate.toJavaInstant()
            it[members] = groupChat.members.joinToString(";") { name -> name.name }
        } > 0
    }

    override suspend fun deleteGroupChat(name: String): Boolean = dbQuery {
        GroupChats.deleteWhere { GroupChats.name eq name } > 0
    }
}