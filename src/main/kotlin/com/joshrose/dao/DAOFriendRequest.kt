package com.joshrose.dao

import com.joshrose.models.FriendRequest

interface DAOFriendRequest {
    suspend fun allFriendRequests(): List<FriendRequest>
    suspend fun sentFriendRequests(requesterId: Int): List<FriendRequest>
    suspend fun receivedFriendRequests(toId: Int): List<FriendRequest>
    suspend fun friendRequest(id: Int): FriendRequest?
    suspend fun friendRequestExists(requesterId: Int, toId: Int): Boolean
    suspend fun addNewFriendRequest(requesterId: Int, toId: Int): FriendRequest?
    suspend fun editFriendRequest(friendRequest: FriendRequest): Boolean
    suspend fun removeFriendRequest(id: Int): Boolean
}