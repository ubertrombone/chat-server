package com.joshrose.dao

import com.joshrose.models.FriendRequest
import com.joshrose.util.Username

interface DAOFriendRequest {
    suspend fun allFriendRequests(): List<FriendRequest>
    suspend fun sentFriendRequests(requesterUsername: Username): List<FriendRequest>
    suspend fun receivedFriendRequests(toUsername: Username): List<FriendRequest>
    suspend fun friendRequest(id: Int): FriendRequest?
    suspend fun friendRequestExists(requesterUsername: Username, toUsername: Username): Boolean
    suspend fun addNewFriendRequest(requesterUsername: Username, toUsername: Username): FriendRequest?
    suspend fun editFriendRequest(friendRequest: FriendRequest): Boolean
    suspend fun removeFriendRequest(id: Int): Boolean
}