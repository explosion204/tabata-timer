package com.explosion204.tabatatimer.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "seq_table")
data class Sequence(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "seqId")
    override val id: Int = 0,

    @ColumnInfo(name = "title")
    override val title: String,

    @ColumnInfo(name = "description")
    override val description: String

) : BaseEntity {

    override fun <T : BaseEntity> isSame(anotherObj: T): Boolean {
        return copy(id = anotherObj.id).hashCode() == anotherObj.hashCode()
    }
}