package com.explosion204.tabatatimer.data.dao

import androidx.room.*
import com.explosion204.tabatatimer.data.entities.BaseEntity

@Dao
interface BaseDao<T : BaseEntity> {
    @Update
    fun update(item: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: T)

    @Delete
    fun delete(item: T)
}