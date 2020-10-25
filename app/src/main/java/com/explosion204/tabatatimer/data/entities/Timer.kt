package com.explosion204.tabatatimer.data.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "timer_table")
data class Timer(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "timerId")
    val timerId: Int = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "preparations")
    val preparations: Int,

    @ColumnInfo(name = "workout")
    val workout: Int,

    @ColumnInfo(name = "rest")
    val rest: Int,

    @ColumnInfo(name = "cycles")
    val cycles: Int
) : Parcelable