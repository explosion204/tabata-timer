package com.explosion204.tabatatimer.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.explosion204.tabatatimer.data.entities.Sequence
import com.explosion204.tabatatimer.data.entities.SequenceWithTimers

@Dao
interface SequenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sequence: Sequence) : Long

    @Delete
    fun delete(sequence: Sequence)

    @Query("DELETE FROM seq_table WHERE seqId IN (:seqIds)")
    fun delete(seqIds: List<Int>)

    @Query("SELECT * FROM seq_table ORDER BY title ASC")
    fun getAll(): LiveData<List<SequenceWithTimers>>

    @Query("SELECT * FROM seq_table WHERE seqId=:id")
    fun get(id: Int) : SequenceWithTimers
}