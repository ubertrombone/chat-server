package com.joshrose.dao

import com.joshrose.models.GroupChat
import java.time.LocalDateTime

interface DAOGroupChat {
    suspend fun allGroupChats(): List<GroupChat>
    suspend fun groupChat(id: Int): GroupChat?
    suspend fun groupChatNameExists(name: String): Boolean
    suspend fun addNewGroupChat(
        name: String,
        creator: String,
        createdDate: LocalDateTime,
        population: Int
    ): GroupChat?
    suspend fun editGroupChat(groupChat: GroupChat): Boolean
    suspend fun deleteGroupChat(id: Int): Boolean
}