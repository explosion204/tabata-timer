package com.explosion204.tabatatimer.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timer_table")
data class Timer(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "timerId")
    override val id: Int,

    @ColumnInfo(name = "title")
    override val title: String,

    @ColumnInfo(name = "description")
    override val description: String,

    @ColumnInfo(name = "preparations")
    val preparations: Int,

    @ColumnInfo(name = "workout")
    val workout: Int,

    @ColumnInfo(name = "rest")
    val rest: Int,

    @ColumnInfo(name = "cycles")
    val cycles: Int
) : BaseEntity {

    override fun <T : BaseEntity> isSame(anotherObj: T): Boolean {
        return copy(id = anotherObj.id).hashCode() == anotherObj.hashCode()
    }
}