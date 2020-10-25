package com.explosion204.tabatatimer.di.modules

import android.app.Application
import com.explosion204.tabatatimer.data.dao.SequenceDao
import com.explosion204.tabatatimer.data.dao.SequenceTimerCrossRefDao
import com.explosion204.tabatatimer.data.dao.TimerDao
import com.explosion204.tabatatimer.data.db.AppDatabase
import com.explosion204.tabatatimer.data.repos.SequenceRepository
import com.explosion204.tabatatimer.data.repos.TimerRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideTimerRepository(timerDao: TimerDao,
                               sequenceTimerCrossRefDao: SequenceTimerCrossRefDao)
            = TimerRepository(timerDao, sequenceTimerCrossRefDao)

    @Provides
    @Singleton
    fun provideSequenceRepository(sequenceDao: SequenceDao,
                                  sequenceTimerCrossRefDao: SequenceTimerCrossRefDao)
            = SequenceRepository(sequenceDao, sequenceTimerCrossRefDao)

    @Provides
    @Singleton
    fun provideTimerDao(database: AppDatabase) = database.timerDao()

    @Provides
    @Singleton
    fun provideSequenceDao(database: AppDatabase) = database.sequenceDao()

    @Provides
    @Singleton
    fun provideSequenceTimerCrossRefDao(database: AppDatabase) = database.sequenceTimerCrossRefDao()

    @Provides
    @Singleton
    fun provideDatabase(application: Application) = AppDatabase.getInstance(application)
}