package com.explosion204.tabatatimer.data.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class SequenceWithTimers(
    @Embedded val sequence: Sequence,
    @Relation(
        parentColumn = "seqId",
        entityColumn = "timerId",
        associateBy = Junction(TimerSequenceCrossRef::class)
    )
    val timers: List<Timer>
)