package com.explosion204.tabatatimer.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "seq_table")
data class Sequence(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "seqId")
    val seqId: Int = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String

)