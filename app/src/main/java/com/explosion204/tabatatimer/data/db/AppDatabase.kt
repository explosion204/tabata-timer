package com.explosion204.tabatatimer.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.explosion204.tabatatimer.data.dao.SequenceDao
import com.explosion204.tabatatimer.data.dao.SequenceTimerCrossRefDao
import com.explosion204.tabatatimer.data.entities.Timer
import com.explosion204.tabatatimer.data.dao.TimerDao
import com.explosion204.tabatatimer.data.entities.Sequence
import com.explosion204.tabatatimer.data.entities.SequenceTimerCrossRef

@Database(entities = [Timer::class, Sequence::class, SequenceTimerCrossRef::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun timerDao(): TimerDao
    abstract fun sequenceDao(): SequenceDao
    abstract fun sequenceTimerCrossRefDao(): SequenceTimerCrossRefDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context) : AppDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }


        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_db"
            ).build()
    }
}