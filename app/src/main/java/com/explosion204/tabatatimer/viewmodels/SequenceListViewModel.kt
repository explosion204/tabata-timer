package com.explosion204.tabatatimer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.explosion204.tabatatimer.data.entities.SequenceWithTimers
import com.explosion204.tabatatimer.data.entities.Timer
import com.explosion204.tabatatimer.data.repos.SequenceRepository
import com.explosion204.tabatatimer.data.repos.TimerRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class SequenceListViewModel @Inject constructor(private val sequenceRepo: SequenceRepository,
                                                private val timerRepo: TimerRepository) :
    BaseViewModel() {

    var selectedItems = ArrayList<SequenceWithTimers>()
    var allTimers = ArrayList<Timer>()
    var recentlyDeletedSequence: SequenceWithTimers? = null

    fun getAll() : LiveData<List<SequenceWithTimers>> {
        return sequenceRepo.getAll()
    }

    fun fetchAllTimers() : LiveData<List<Timer>> {
        return timerRepo.getAll()
    }

    fun insert(sequence: SequenceWithTimers) {
        viewModelScope.launch {
            sequenceRepo.insert(sequence)
        }
    }

    fun delete(sequence: SequenceWithTimers) {
        viewModelScope.launch {
            sequenceRepo.delete(sequence)
        }
    }

    fun delete() {
        viewModelScope.launch {
            sequenceRepo.delete(selectedItems)
        }
    }
}
