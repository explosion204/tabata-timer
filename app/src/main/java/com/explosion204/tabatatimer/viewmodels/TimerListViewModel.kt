package com.explosion204.tabatatimer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.explosion204.tabatatimer.data.entities.Timer
import com.explosion204.tabatatimer.data.repos.TimerRepository
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.ArrayList

class TimerListViewModel @Inject constructor(private val timerRepo: TimerRepository) : BaseViewModel()
{
    var selectedItems = ArrayList<Timer>()
    var recentlyDeletedTimer: Timer? = null

    fun getAll() : LiveData<List<Timer>> {
        return timerRepo.getAll()
    }

    fun insertTimer(timer: Timer) {
        viewModelScope.launch {
            timerRepo.insert(timer)
        }
    }

    fun delete(timer: Timer) {
        viewModelScope.launch {
            timerRepo.delete(timer)
        }
    }

    fun delete() {
        viewModelScope.launch {
            timerRepo.delete(selectedItems)
        }
    }
}
