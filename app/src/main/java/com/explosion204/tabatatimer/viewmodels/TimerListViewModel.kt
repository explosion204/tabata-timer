package com.explosion204.tabatatimer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.explosion204.tabatatimer.data.dao.TimerDao
import com.explosion204.tabatatimer.data.entities.Timer
import com.explosion204.tabatatimer.data.repos.TimerRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class TimerListViewModel @Inject constructor(private val timerRepo: TimerRepository) :
    ListViewModel<Timer, TimerDao>(timerRepo) {

    fun getAll() : LiveData<List<Timer>> {
        return timerRepo.getAll()
    }
}
