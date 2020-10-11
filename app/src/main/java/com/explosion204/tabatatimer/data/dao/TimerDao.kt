package com.explosion204.tabatatimer.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.explosion204.tabatatimer.data.entities.Timer

@Dao
interface TimerDao {
    @Query("SELECT * FROM timer_table ORDER BY title DESC")
    fun getAll(): LiveData<List<Timer>>

    @Update
    fun update(timer: Timer)

    @Insert
    fun insert(timer: Timer)

    @Delete
    fun delete(timer: Timer)
}