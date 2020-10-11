package com.explosion204.tabatatimer.viewmodels

import android.app.Application
import androidx.annotation.NonNull
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.explosion204.tabatatimer.data.dao.SequenceDao
import com.explosion204.tabatatimer.data.entities.Sequence
import com.explosion204.tabatatimer.data.entities.SequenceWithTimers
import com.explosion204.tabatatimer.data.repos.SequenceRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class SequenceListViewModel @Inject constructor(private val sequenceRepo: SequenceRepository) :
    ListViewModel<Sequence, SequenceDao>(sequenceRepo) {

    fun getAll() : LiveData<List<SequenceWithTimers>> {
        return sequenceRepo.getAll()
    }
}
