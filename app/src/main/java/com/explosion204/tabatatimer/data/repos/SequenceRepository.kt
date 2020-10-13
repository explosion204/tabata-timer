package com.explosion204.tabatatimer.data.repos

import androidx.lifecycle.LiveData
import com.explosion204.tabatatimer.data.dao.SequenceDao
import com.explosion204.tabatatimer.data.entities.Sequence
import com.explosion204.tabatatimer.data.entities.SequenceWithTimers
import javax.inject.Inject

class SequenceRepository(private val sequenceDao: SequenceDao) :
    BaseRepository<Sequence, SequenceDao>(sequenceDao) {

    private var allSequences: LiveData<List<Sequence>> = sequenceDao.getAll()

    fun getAll() : LiveData<List<Sequence>> {
        return allSequences
    }
}