package com.joshrose.dao

import com.joshrose.dao.DatabaseFactory.dbQuery
import com.joshrose.models.FriendRequest
import com.joshrose.models.FriendRequests
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class DAOFriendRequestImpl : DAOFriendRequest {
    private fun resultRowToFriendRequest(row: ResultRow) = FriendRequest(
        id = row[FriendRequests.id],
        requesterId = row[FriendRequests.requesterId],
        toId = row[FriendRequests.toId]
    )

    override suspend fun allFriendRequests(): Set<FriendRequest> = dbQuery {
        FriendRequests.selectAll().map(::resultRowToFriendRequest).toSet()
    }

    override suspend fun sentFriendRequests(requesterId: Int): Set<FriendRequest> = dbQuery {
        FriendRequests.selectAll().where { FriendRequests.requesterId eq requesterId }.map(::resultRowToFriendRequest).toSet()
    }

    override suspend fun receivedFriendRequests(toId: Int): Set<FriendRequest> = dbQuery {
        FriendRequests.selectAll().where { FriendRequests.toId eq toId }.map(::resultRowToFriendRequest).toSet()
    }

    override suspend fun friendRequest(id: Int): FriendRequest? = dbQuery {
        FriendRequests.selectAll()
            .where { FriendRequests.id eq id }
            .map(::resultRowToFriendRequest)
            .singleOrNull()
    }

    override suspend fun friendRequestExists(requesterId: Int, toId: Int): Boolean = dbQuery {
        FriendRequests.selectAll()
            .where { (FriendRequests.requesterId eq requesterId) and (FriendRequests.toId eq toId) }
            .count().toInt() > 0
    }

    override suspend fun addNewFriendRequest(requesterId: Int, toId: Int): FriendRequest? = dbQuery {
        val insertStatement = FriendRequests.insert {
            it[FriendRequests.requesterId] = requesterId
            it[FriendRequests.toId] = toId
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToFriendRequest)
    }

    override suspend fun editFriendRequest(friendRequest: FriendRequest): Boolean = dbQuery {
        FriendRequests.update({ FriendRequests.id eq friendRequest.id }) {
            it[requesterId] = friendRequest.requesterId
            it[toId] = friendRequest.toId
        } > 0
    }

    override suspend fun removeFriendRequest(requesterId: Int, toId: Int): Boolean = dbQuery {
        FriendRequests.deleteWhere { (FriendRequests.toId eq toId) and (FriendRequests.requesterId eq requesterId) } > 0
    }

    override suspend fun deleteUserFromRequests(userId: Int): Boolean = dbQuery {
        FriendRequests.deleteWhere { (toId eq userId) or (requesterId eq userId) } > 0
    }
}