package com.explosion204.tabatatimer.data.db

import android.content.Context
import androidx.room.Room

object AppDatabaseProvider {
    private var instance: AppDatabase? = null

    fun getInstance(context: Context) : AppDatabase {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_db"
            ).build()
        }

        return instance!!
    }
}