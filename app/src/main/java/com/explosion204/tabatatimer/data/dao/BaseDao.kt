package com.explosion204.tabatatimer.data.dao

import androidx.room.*

@Dao
interface BaseDao<T> {
    @Update
    fun update(item: T)

    @Insert
    fun insert(item: T)

    @Delete
    fun delete(item: T)
}