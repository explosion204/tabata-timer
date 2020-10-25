package com.explosion204.tabatatimer.data.repos

import androidx.lifecycle.LiveData
import com.explosion204.tabatatimer.data.dao.SequenceDao
import com.explosion204.tabatatimer.data.dao.SequenceTimerCrossRefDao
import com.explosion204.tabatatimer.data.entities.SequenceWithTimers
import com.explosion204.tabatatimer.data.entities.SequenceTimerCrossRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SequenceRepository(private val sequenceDao: SequenceDao,
                         private val sequenceTimerCrossRefDao: SequenceTimerCrossRefDao) {

    private var allSequences: LiveData<List<SequenceWithTimers>> = sequenceDao.getAll()

    fun getAll() : LiveData<List<SequenceWithTimers>> {
        return allSequences
    }

    fun get(id: Int) : SequenceWithTimers {
        return sequenceDao.get(id)
    }

    suspend fun insert(sequenceWithTimers: SequenceWithTimers) {
        return withContext(Dispatchers.IO) {
            sequenceTimerCrossRefDao.deleteBySeqId(sequenceWithTimers.sequence.seqId)
            val seqId = sequenceDao.insert(sequenceWithTimers.sequence).toInt()

            for (timer in sequenceWithTimers.timers) {
                sequenceTimerCrossRefDao.insert(
                    SequenceTimerCrossRef(
                    seqId = seqId,
                    timerId = timer.timerId
                ))
            }
        }
    }

    suspend fun delete(sequenceWithTimers: SequenceWithTimers) {
        return withContext(Dispatchers.IO) {
            sequenceTimerCrossRefDao.deleteBySeqId(sequenceWithTimers.sequence.seqId)
            sequenceDao.delete(sequenceWithTimers.sequence)
        }
    }

    suspend fun delete(items: ArrayList<SequenceWithTimers>) {
        val seqIds = ArrayList<Int>()
        for (item in items) {
            seqIds.add(item.sequence.seqId)
        }

        return withContext(Dispatchers.IO) {
            sequenceTimerCrossRefDao.deleteBySeqId(seqIds)
            sequenceDao.delete(seqIds)
        }
    }
}