package com.explosion204.tabatatimer.data.repos

import com.explosion204.tabatatimer.data.dao.BaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

open class BaseRepository<T, D : BaseDao<T>>(private val dao: D) {
    suspend fun insert(item: T) {
        return withContext(Dispatchers.IO) {
            dao.insert(item)
        }
    }

    suspend fun update(item: T) {
        return withContext(Dispatchers.IO) {
            dao.update(item)
        }
    }

    suspend fun delete(item: T) {
        return withContext(Dispatchers.IO) {
            dao.delete(item)
        }
    }


}