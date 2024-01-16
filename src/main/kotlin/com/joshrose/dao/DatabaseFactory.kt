package com.joshrose.dao

import com.joshrose.models.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

object DatabaseFactory {
    fun init(driverClassName: String, jdbcURL: String) {
        connect(driverClassName, jdbcURL)
        createSchema()
    }

    private fun connect(driverClassName: String, jdbcURL: String) {
        Database.connect(jdbcURL, driverClassName)
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }

    private fun createSchema() {
        transaction {
            SchemaUtils.create(Users, FriendRequests, GroupChats, Archives, Chats, Caches)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}