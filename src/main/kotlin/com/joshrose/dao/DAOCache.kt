package com.joshrose.dao

import com.joshrose.models.Cache

interface DAOCache {
    suspend fun allCache(): List<Cache>
    // TODO: A user should be able to delete a chat and all of its cache; also when profile is deleted
    suspend fun cache(id: Int): Cache?
    suspend fun add(message: String, sender: Int, primaryUser: Int, error: Int?, chat: Int): Cache?
    suspend fun edit(cache: Cache): Boolean
    suspend fun delete(id: Int): Boolean
    suspend fun deleteAllWhere(primaryUser: Int): Boolean
    suspend fun deleteCacheOf(chat: Int): Boolean
}