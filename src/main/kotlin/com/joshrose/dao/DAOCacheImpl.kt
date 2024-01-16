package com.joshrose.dao

import com.joshrose.dao.DatabaseFactory.dbQuery
import com.joshrose.models.Cache
import com.joshrose.models.Caches
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class DAOCacheImpl : DAOCache {
    private fun resultRowToCache(row: ResultRow) = Cache(
        id = row[Caches.id],
        message = row[Caches.message],
        sender = row[Caches.sender],
        timestamp = row[Caches.timestamp].toKotlinInstant(),
        primaryUserReference = row[Caches.primaryUserReference],
        chat = row[Caches.chat]
    )

    override suspend fun allCache(): List<Cache> = dbQuery {
        Caches.selectAll().map(::resultRowToCache)
    }

    override suspend fun cache(id: Int): Cache? = dbQuery  {
        Caches.selectAll().where { Caches.id eq id }.map(::resultRowToCache).singleOrNull()
    }

    override suspend fun add(message: String, sender: Int, primaryUser: Int, chat: Int): Cache? = dbQuery  {
        Caches.insert {
            it[this.message] = message
            it[this.sender] = sender
            it[primaryUserReference] = primaryUser
            it[this.chat] = chat
            it[timestamp] = Clock.System.now().toJavaInstant()
        }.resultedValues?.singleOrNull()?.let(::resultRowToCache)
    }

    override suspend fun edit(cache: Cache): Boolean = dbQuery  {
        Caches.update({ Caches.id eq cache.id }) {
            it[message] = cache.message
            it[sender] = cache.sender
            it[primaryUserReference] = cache.primaryUserReference
            it[chat] = cache.chat
            it[timestamp] = cache.timestamp.toJavaInstant()
        } > 0
    }

    override suspend fun delete(cache: Cache): Boolean = dbQuery  {
        Caches.deleteWhere { id eq cache.id } > 0
    }

    override suspend fun deleteAllWhere(primaryUser: Int): Boolean = dbQuery  {
        Caches.deleteWhere { primaryUserReference eq primaryUser } > 0
    }
}