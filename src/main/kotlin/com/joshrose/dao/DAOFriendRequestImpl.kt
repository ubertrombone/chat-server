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
    override suspend fun allFriendRequests(): List<FriendRequest> = dbQuery {
        FriendRequests.selectAll().map(::resultRowToFriendRequest)
    }

    override suspend fun sentFriendRequests(requesterId: String): List<FriendRequest> = dbQuery {
        FriendRequests
            .select { FriendRequests.requesterId eq requesterId }
            .map(::resultRowToFriendRequest)
    }

    override suspend fun receivedFriendRequests(toId: String): List<FriendRequest> = dbQuery {
        FriendRequests
            .select { FriendRequests.toId eq toId }
            .map(::resultRowToFriendRequest)
    }

    override suspend fun friendRequest(id: Int): FriendRequest? = dbQuery {
        FriendRequests
            .select { FriendRequests.id eq id }
            .map(::resultRowToFriendRequest)
            .singleOrNull()
    }

    override suspend fun friendRequestExists(requesterId: String, toId: String): Boolean = dbQuery {
        FriendRequests
            .select { FriendRequests.requesterId eq requesterId and (FriendRequests.toId eq toId) }
            .count().toInt() > 0
    }

    override suspend fun addNewFriendRequest(requesterId: String, toId: String): FriendRequest? = dbQuery {
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

    override suspend fun removeFriendRequest(id: Int): Boolean = dbQuery {
        FriendRequests.deleteWhere { FriendRequests.id eq id } > 0
    }
}