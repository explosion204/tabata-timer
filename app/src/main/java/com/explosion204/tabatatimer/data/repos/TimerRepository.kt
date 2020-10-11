package com.explosion204.tabatatimer.data.repos

import android.app.Application
import androidx.lifecycle.LiveData
import com.explosion204.tabatatimer.data.dao.TimerDao
import com.explosion204.tabatatimer.data.db.AppDatabase
import com.explosion204.tabatatimer.data.db.AppDatabaseProvider
import com.explosion204.tabatatimer.data.entities.Timer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TimerRepository(application: Application) {
    private var timerDao: TimerDao
    private var appDatabase: AppDatabase
    private var allTimers: LiveData<List<Timer>>

    init {
        appDatabase = AppDatabaseProvider.getInstance(application)
        timerDao = appDatabase.timerDao()
        allTimers = timerDao.getAll()
    }

    suspend fun insert(timer: Timer) {
        return withContext(Dispatchers.IO) {
            timerDao.insert(timer)
        }
    }

    suspend fun update(timer: Timer) {
        return withContext(Dispatchers.IO) {
            timerDao.update(timer)
        }
    }

    suspend fun delete(timer: Timer) {
        return withContext(Dispatchers.IO) {
            timerDao.delete(timer)
        }
    }

    fun getAll() : LiveData<List<Timer>> {
        return allTimers
    }
}