package com.explosion204.tabatatimer.viewmodels

import android.app.Application
import androidx.annotation.NonNull
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.explosion204.tabatatimer.data.entities.Timer
import com.explosion204.tabatatimer.data.repos.TimerRepository
import kotlinx.coroutines.launch

class TimerViewModel(
    @NonNull application: Application,
    private val timerRepo: TimerRepository,
    private val allTimers: LiveData<List<Timer>>
) : AndroidViewModel(application) {

    fun insert(timer: Timer) {
        viewModelScope.launch {
            timerRepo.insert(timer)
        }
    }

    fun update(timer: Timer) {
        viewModelScope.launch {
            timerRepo.update(timer)
        }
    }

    fun delete(timer: Timer) {
        viewModelScope.launch {
            timerRepo.delete(timer)
        }
    }

    fun getAllTimers() : LiveData<List<Timer>> {
        return timerRepo.getAll()
    }
}
