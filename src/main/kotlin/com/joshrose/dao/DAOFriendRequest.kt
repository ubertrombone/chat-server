package com.joshrose.dao

import com.joshrose.models.FriendRequest

interface DAOFriendRequest {
    suspend fun allFriendRequests(): Set<FriendRequest>
    suspend fun sentFriendRequests(requesterId: Int): Set<FriendRequest>
    suspend fun receivedFriendRequests(toId: Int): Set<FriendRequest>
    suspend fun friendRequest(id: Int): FriendRequest?
    suspend fun friendRequestExists(requesterId: Int, toId: Int): Boolean
    suspend fun addNewFriendRequest(requesterId: Int, toId: Int): FriendRequest?
    suspend fun editFriendRequest(friendRequest: FriendRequest): Boolean
    suspend fun removeFriendRequest(id: Int): Boolean
    suspend fun removeFriendRequest(requesterId: Int, toId: Int): Boolean
    suspend fun deleteUserFromRequests(userId: Int): Boolean
}