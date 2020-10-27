package com.explosion204.tabatatimer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.explosion204.tabatatimer.data.entities.Timer
import com.explosion204.tabatatimer.data.repos.SequenceRepository
import com.explosion204.tabatatimer.services.TimerPhase
import com.explosion204.tabatatimer.services.TimerService
import kotlinx.coroutines.launch
import javax.inject.Inject

class TimerViewModel @Inject constructor(private val sequenceRepository: SequenceRepository)
    : BaseViewModel() {
    var timersCount = 0
    var allTimers = ArrayList<Timer>()
    var currentTimerPos = MutableLiveData(0)
    var currentTimer = MutableLiveData<Timer>()
    var currentPhase = MutableLiveData(TimerPhase.PREPARATION)
    var preparationRemaining = MutableLiveData(0)
    var workoutRemaining = MutableLiveData(0)
    var restRemaining = MutableLiveData(0)
    var cyclesRemaining = MutableLiveData(0)

}