package com.explosion204.tabatatimer.data.repos

import androidx.lifecycle.LiveData
import com.explosion204.tabatatimer.data.dao.SequenceTimerCrossRefDao
import com.explosion204.tabatatimer.data.dao.TimerDao
import com.explosion204.tabatatimer.data.entities.Timer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TimerRepository(private val timerDao: TimerDao,
                      private val sequenceTimerCrossRefDao: SequenceTimerCrossRefDao) {

    private var allTimers: LiveData<List<Timer>> = timerDao.getAll()

    fun getAll() : LiveData<List<Timer>> {
        return allTimers
    }

    suspend fun insert(timer: Timer) {
        return withContext(Dispatchers.IO) {
            timerDao.insert(timer)
        }
    }

    suspend fun get(id: Int) : Timer {
        return withContext(Dispatchers.IO) {
            timerDao.get(id)
        }
    }

    suspend fun delete(timer: Timer) {
        return withContext(Dispatchers.IO) {
            timerDao.delete(timer)
            sequenceTimerCrossRefDao.deleteByTimerId(timer.timerId)
        }
    }

    suspend fun delete(items: List<Timer>) {
        val timerIds = ArrayList<Int>()
        for (timer in items) {
            timerIds.add(timer.timerId)
        }

        return withContext(Dispatchers.IO) {
            timerDao.delete(timerIds)
            sequenceTimerCrossRefDao.deleteByTimerId(timerIds)
        }
    }
}