package com.explosion204.tabatatimer.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timer_table")
data class Timer(
    @PrimaryKey(autoGenerate = true)
    val timerId: Int,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "preparations")
    val preparations: Int,

    @ColumnInfo(name = "workout")
    val workout: Int,

    @ColumnInfo(name = "rest")
    val rest: Int,

    @ColumnInfo(name = "cycles")
    val cycles: Int
)