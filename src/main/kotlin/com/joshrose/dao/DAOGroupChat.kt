package com.joshrose.dao

import com.joshrose.models.GroupChat
import com.joshrose.util.Username
import java.time.LocalDateTime

interface DAOGroupChat {
    suspend fun allGroupChats(): Set<GroupChat>
    suspend fun groupChat(name: String): GroupChat?
    suspend fun groupChatNameExists(name: String): Boolean
    suspend fun addNewGroupChat(
        name: String,
        creator: Username,
        createdDate: LocalDateTime,
        members: Set<String>
    ): GroupChat?
    suspend fun editGroupChat(groupChat: GroupChat): Boolean
    suspend fun deleteGroupChat(name: String): Boolean
}