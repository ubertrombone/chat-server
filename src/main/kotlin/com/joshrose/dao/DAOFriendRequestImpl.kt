package com.joshrose.dao

import com.joshrose.dao.DatabaseFactory.dbQuery
import com.joshrose.models.FriendRequest
import com.joshrose.models.FriendRequests
import com.joshrose.util.Username
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class DAOFriendRequestImpl : DAOFriendRequest {
    private fun resultRowToFriendRequest(row: ResultRow) = FriendRequest(
        id = row[FriendRequests.id],
        requesterUsername = row[FriendRequests.requesterUsername],
        toUsername = row[FriendRequests.toUsername]
    )
    override suspend fun allFriendRequests(): List<FriendRequest> = dbQuery {
        FriendRequests.selectAll().map(::resultRowToFriendRequest)
    }

    override suspend fun sentFriendRequests(requesterUsername: Username): List<FriendRequest> = dbQuery {
        FriendRequests
            .select { FriendRequests.requesterUsername eq requesterUsername.name }
            .map(::resultRowToFriendRequest)
    }

    override suspend fun receivedFriendRequests(toUsername: Username): List<FriendRequest> = dbQuery {
        FriendRequests
            .select { FriendRequests.toUsername eq toUsername.name }
            .map(::resultRowToFriendRequest)
    }

    override suspend fun friendRequest(id: Int): FriendRequest? = dbQuery {
        FriendRequests
            .select { FriendRequests.id eq id }
            .map(::resultRowToFriendRequest)
            .singleOrNull()
    }

    override suspend fun friendRequestExists(requesterUsername: Username, toUsername: Username): Boolean = dbQuery {
        FriendRequests
            .select {
                FriendRequests.requesterUsername eq requesterUsername.name and (FriendRequests.toUsername eq toUsername.name)
            }
            .count().toInt() > 0
    }

    override suspend fun addNewFriendRequest(requesterUsername: Username, toUsername: Username): FriendRequest? = dbQuery {
        val insertStatement = FriendRequests.insert {
            it[FriendRequests.requesterUsername] = requesterUsername.name
            it[FriendRequests.toUsername] = toUsername.name
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToFriendRequest)
    }

    override suspend fun editFriendRequest(friendRequest: FriendRequest): Boolean = dbQuery {
        FriendRequests.update({ FriendRequests.id eq friendRequest.id }) {
            it[requesterUsername] = friendRequest.requesterUsername
            it[toUsername] = friendRequest.toUsername
        } > 0
    }

    override suspend fun removeFriendRequest(id: Int): Boolean = dbQuery {
        FriendRequests.deleteWhere { FriendRequests.id eq id } > 0
    }
}