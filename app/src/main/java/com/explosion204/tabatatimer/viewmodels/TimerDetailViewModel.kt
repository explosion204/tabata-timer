package com.explosion204.tabatatimer.viewmodels

import androidx.lifecycle.viewModelScope
import com.explosion204.tabatatimer.data.entities.Timer
import com.explosion204.tabatatimer.data.repos.TimerRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class TimerDetailViewModel @Inject constructor(private val timerRepository: TimerRepository) :
    BaseViewModel() {

    var id = 0
    var title = ""
    var desc = ""
    var color = -10354450
    var prep = 0
    var workout = 1
    var rest = 0
    var cycles = 1


    fun saveTimer() : Boolean {
        if (title.isNotEmpty() and desc.isNotEmpty()) {
            val timer = Timer(
                timerId = id,
                title = title,
                description = desc,
                preparations = prep,
                workout = workout,
                rest = rest,
                cycles = cycles,
                color = color)

            viewModelScope.launch {
                timerRepository.insert(timer)
            }

            return true
        }

        return false
    }
}