package com.joshrose.dao

import com.joshrose.dao.DatabaseFactory.dbQuery
import com.joshrose.models.FriendRequest
import com.joshrose.models.FriendRequests
import com.joshrose.util.Username
import com.joshrose.util.toUsername
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class DAOFriendRequestImpl : DAOFriendRequest {
    private fun resultRowToFriendRequest(row: ResultRow) = FriendRequest(
        id = row[FriendRequests.id],
        requesterUsername = row[FriendRequests.requesterUsername].toUsername(),
        toUsername = row[FriendRequests.toUsername].toUsername()
    )
    override suspend fun allFriendRequests(): Set<FriendRequest> = dbQuery {
        FriendRequests.selectAll().map(::resultRowToFriendRequest).toSet()
    }

    override suspend fun sentFriendRequests(requesterUsername: Username): Set<FriendRequest> = dbQuery {
        FriendRequests
            .select { FriendRequests.requesterUsername.lowerCase() eq requesterUsername.name.lowercase() }
            .map(::resultRowToFriendRequest).toSet()
    }

    override suspend fun receivedFriendRequests(toUsername: Username): Set<FriendRequest> = dbQuery {
        FriendRequests
            .select { FriendRequests.toUsername.lowerCase() eq toUsername.name.lowercase() }
            .map(::resultRowToFriendRequest).toSet()
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
            it[requesterUsername] = friendRequest.requesterUsername.name
            it[toUsername] = friendRequest.toUsername.name
        } > 0
    }

    override suspend fun removeFriendRequest(fromUsername: Username, toUsername: Username): Boolean = dbQuery {
        FriendRequests.deleteWhere {
            (FriendRequests.toUsername eq toUsername.name) and (FriendRequests.requesterUsername eq fromUsername.name)
        } > 0
    }
}