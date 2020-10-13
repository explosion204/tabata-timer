package com.explosion204.tabatatimer.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.explosion204.tabatatimer.data.entities.Timer

@Dao
interface TimerDao : BaseDao<Timer> {
    @Query("SELECT * FROM timer_table ORDER BY title ASC")
    fun getAll(): LiveData<List<Timer>>

    @Query("SELECT * FROM timer_table WHERE timerId=:id")
    fun get(id: Int) : LiveData<Timer>
}