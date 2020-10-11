package com.explosion204.tabatatimer.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.explosion204.tabatatimer.data.entities.Sequence
import com.explosion204.tabatatimer.data.entities.SequenceWithTimers

@Dao
interface SequenceDao : BaseDao<Sequence> {
    @Query("SELECT * FROM seq_table ORDER BY title DESC")
    fun getAll(): LiveData<List<SequenceWithTimers>>

    @Query("SELECT * FROM seq_table WHERE seqId=:id")
    fun get(id: Int) : LiveData<SequenceWithTimers>
}