package com.explosion204.tabatatimer.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "seq_table")
data class Sequence(
    @PrimaryKey
    val seqId: Int,
    @ColumnInfo(name = "title")
    val title: String
)