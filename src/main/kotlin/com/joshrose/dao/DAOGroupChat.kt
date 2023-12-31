package com.joshrose.dao

import com.joshrose.models.GroupChat
import com.joshrose.util.Username
import kotlinx.datetime.Instant

interface DAOGroupChat {
    suspend fun allGroupChats(): Set<GroupChat>
    suspend fun groupChat(name: String): GroupChat?
    suspend fun groupChatNameExists(name: String): Boolean
    suspend fun addNewGroupChat(
        name: String,
        creator: Username,
        createdDate: Instant,
        members: Set<Username>
    ): GroupChat?
    suspend fun editGroupChat(groupChat: GroupChat): Boolean
    suspend fun deleteGroupChat(name: String): Boolean
}