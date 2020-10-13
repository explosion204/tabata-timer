package com.explosion204.tabatatimer.data.repos

import androidx.lifecycle.LiveData
import com.explosion204.tabatatimer.data.dao.TimerDao
import com.explosion204.tabatatimer.data.entities.Timer
import javax.inject.Inject

class TimerRepository(private val timerDao: TimerDao) :
    BaseRepository<Timer, TimerDao>(timerDao) {

    private var allTimers: LiveData<List<Timer>> = timerDao.getAll()

    fun getAll() : LiveData<List<Timer>> {
        return allTimers
    }
}