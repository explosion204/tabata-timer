package com.explosion204.tabatatimer.data.entities

import androidx.room.Entity

@Entity(
    tableName = "sequence_timer",
    primaryKeys = ["timerId", "seqId"]
)
data class SequenceTimerCrossRef(
    val timerId: Int,
    val seqId: Int
)