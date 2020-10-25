package com.explosion204.tabatatimer.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.explosion204.tabatatimer.data.entities.Sequence
import com.explosion204.tabatatimer.data.entities.Timer
import com.explosion204.tabatatimer.data.entities.SequenceWithTimers
import com.explosion204.tabatatimer.data.repos.SequenceRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class SequenceDetailViewModel @Inject constructor(private val sequenceRepo: SequenceRepository)
    : BaseViewModel() {

    var id = 0
    var title = ""
    var desc = ""

    private var _associatedTimers = mutableListOf<Timer>()
    var associatedTimers: MutableLiveData<MutableList<Timer>>? = null

    private var deletedTimers = ArrayList<Timer>()
    private lateinit var allTimers: ArrayList<Timer>

    fun setTimers(associatedTimers: ArrayList<Timer>, allTimers: ArrayList<Timer>) {
        _associatedTimers = associatedTimers
        this.associatedTimers = MutableLiveData(associatedTimers)
        this.allTimers = allTimers
    }

    fun addToAssociated(timers: ArrayList<Timer>) {
        _associatedTimers.addAll(timers)

        if (associatedTimers != null) {
            associatedTimers!!.value = _associatedTimers
        }
    }

    fun delete(timer: Timer) {
        _associatedTimers.remove(timer)
        deletedTimers.add(timer)

        if (associatedTimers != null) {
            associatedTimers!!.value = _associatedTimers
        }
    }

    fun getNotAssociatedTimers(): MutableLiveData<List<Timer>> {
        return MutableLiveData(allTimers.subtract(_associatedTimers).toList())
    }

    fun saveSequence() : Boolean {
        if (title.isNotEmpty() && desc.isNotEmpty() && _associatedTimers.size > 0) {
            val sequence = Sequence(
                seqId = id,
                title = title,
                description = desc
            )

            val sequenceWithTimers = SequenceWithTimers(
                sequence = sequence,
                timers = _associatedTimers
            )

            viewModelScope.launch {
                sequenceRepo.insert(sequenceWithTimers)
            }

            return true
        }

        return false
    }
}