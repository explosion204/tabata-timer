package com.explosion204.tabatatimer.data.entities

import androidx.room.Entity

@Entity(primaryKeys = ["timerId", "seqId"])
data class TimerSequenceCrossRef(
    val timerId: Int,
    val seqId: Int
)