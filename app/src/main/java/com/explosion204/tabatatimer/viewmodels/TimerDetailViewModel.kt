package com.explosion204.tabatatimer.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.explosion204.tabatatimer.data.entities.Timer
import com.explosion204.tabatatimer.data.repos.TimerRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class TimerDetailViewModel @Inject constructor(private val timerRepository: TimerRepository) :
    ViewModel() {

    var title = ""
    var desc = ""
    var prep = 0
    var workout = 0
    var rest = 0
    var cycles = 0


    fun saveTimer() : Boolean {
        if (title.isNotEmpty() and desc.isNotEmpty()) {
            val timer = Timer(title = title,
                description = desc,
                preparations = prep,
                workout = workout,
                rest = rest,
                cycles = cycles)

            viewModelScope.launch {
                timerRepository.insert(timer)
            }

            return true
        }

        return false
    }
}