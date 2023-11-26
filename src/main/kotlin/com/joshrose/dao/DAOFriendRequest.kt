package com.joshrose.dao

import com.joshrose.models.FriendRequest

interface DAOFriendRequest {
    suspend fun allFriendRequests(): List<FriendRequest>
    suspend fun sentFriendRequests(requesterId: String): List<FriendRequest>
    suspend fun receivedFriendRequests(toId: String): List<FriendRequest>
    suspend fun friendRequest(id: Int): FriendRequest?
    suspend fun friendRequestExists(requesterId: String, toId: String): Boolean
    suspend fun addNewFriendRequest(requesterId: String, toId: String): FriendRequest?
    suspend fun editFriendRequest(friendRequest: FriendRequest): Boolean
    suspend fun removeFriendRequest(id: Int): Boolean
}