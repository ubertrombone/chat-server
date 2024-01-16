package com.joshrose.dao

import com.joshrose.models.Chat

interface DAOChats {
    suspend fun allChats(): List<Chat>
    suspend fun chatFromID(id: Int): Chat?
    suspend fun chatFromUsername(with: Int): Chat?
    suspend fun addChat(with: Int): Chat?
    suspend fun editChat(chat: Chat): Boolean
    suspend fun deleteChat(id: Int): Boolean
}