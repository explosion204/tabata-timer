package com.explosion204.tabatatimer.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.explosion204.tabatatimer.data.entities.Timer
import com.explosion204.tabatatimer.data.dao.TimerDao
import java.util.concurrent.Executors

@Database(entities = [Timer::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun timerDao(): TimerDao
}