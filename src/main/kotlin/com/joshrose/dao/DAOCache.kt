package com.joshrose.dao

import com.joshrose.models.Cache

interface DAOCache {
    suspend fun allCache(): List<Cache>
    suspend fun cache(id: Int): Cache?
    suspend fun add(message: String, sender: Int, primaryUser: Int, chat: Int): Cache?
    suspend fun edit(cache: Cache): Boolean
    suspend fun delete(cache: Cache): Boolean
    suspend fun deleteAllWhere(primaryUser: Int): Boolean
}