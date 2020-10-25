package com.explosion204.tabatatimer.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.explosion204.tabatatimer.data.entities.SequenceTimerCrossRef

@Dao
interface SequenceTimerCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sequenceTimerCrossRef: SequenceTimerCrossRef) : Long

    @Query("DELETE FROM sequence_timer WHERE seqId=:seqId")
    fun deleteBySeqId(seqId: Int)

    @Query("DELETE FROM sequence_timer WHERE seqId IN (:seqIds)")
    fun deleteBySeqId(seqIds: List<Int>)

    @Query("DELETE FROM sequence_timer WHERE timerId=:timerId")
    fun deleteByTimerId(timerId: Int)

    @Query("DELETE FROM sequence_timer WHERE timerId IN (:timerIds)")
    fun deleteByTimerId(timerIds: List<Int>)
}