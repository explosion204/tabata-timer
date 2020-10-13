package com.explosion204.tabatatimer.data.entities

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

interface BaseEntity
{
    val id: Int
    val title: String
    val description: String
    fun <T : BaseEntity> isSame(anotherObj: T) : Boolean
}