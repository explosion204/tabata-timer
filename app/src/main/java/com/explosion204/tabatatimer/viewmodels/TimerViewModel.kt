package com.explosion204.tabatatimer.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.explosion204.tabatatimer.data.entities.SequenceWithTimers
import com.explosion204.tabatatimer.data.entities.Timer
import com.explosion204.tabatatimer.data.repos.SequenceRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class TimerViewModel @Inject constructor(private val sequenceRepository: SequenceRepository) : BaseViewModel() {
    interface TimerCallback {
        fun onStart()
        fun onStop()
    }

    private var timerCallback: TimerCallback? = null

    fun setTimerCallback(callback: TimerCallback) {
        timerCallback = callback
    }

    private lateinit var sequence: SequenceWithTimers
    private var currentTimerHandler: TimerHandler? = null

    var timersCount = 0
    var currentTimerPos = MutableLiveData(0)
    var allUpcomingTimers = MutableLiveData<ArrayList<Timer>>()
    var currentTimer = MutableLiveData<Timer>()
    var cyclesRemaining = MutableLiveData(0)
    var currentPhase = MutableLiveData(TimerPhase.PREPARATION)
    var currentPhaseRemaining = MutableLiveData(0)

    enum class TimerPhase {
        PREPARATION, WORKOUT, REST
    }

    fun initViewModel(seqId: Int) {
        viewModelScope.launch {
            sequence = sequenceRepository.get(seqId)
            timersCount = sequence.timers.size
            allUpcomingTimers.value = ArrayList(sequence.timers)
            currentTimerPos.value = 0
            currentTimer.value = sequence.timers[0]
            cyclesRemaining.value = currentTimer.value!!.cycles
            currentPhaseRemaining.value = currentTimer.value!!.preparations
        }
    }

    fun start() {
        currentTimerHandler = TimerHandler(currentPhaseRemaining.value!!.toLong() * 1000 + 1000)
        currentTimerHandler!!.setEventCallback(TimerEventCallback())
        currentTimerHandler!!.start()
        timerCallback?.onStart()
    }

    fun stop() {
        currentTimerHandler?.cancel()
        timerCallback?.onStop()
    }

    fun nextTimer(withoutStopping: Boolean) {
        if (currentTimerPos.value!! + 1 < timersCount) {
            currentTimerPos.value = currentTimerPos.value!! + 1
            currentTimer.value = sequence.timers[currentTimerPos.value!!]
            cyclesRemaining.value = currentTimer.value!!.cycles
            selectPhase(TimerPhase.PREPARATION, withoutStopping)
        }
    }

    fun prevTimer(withoutStopping: Boolean) {
        if (currentTimerPos.value!! - 1 >= 0) {
            currentTimerPos.value = currentTimerPos.value!! - 1
            currentTimer.value = sequence.timers[currentTimerPos.value!!]
            cyclesRemaining.value = currentTimer.value!!.cycles
            selectPhase(TimerPhase.PREPARATION, withoutStopping)
        }
    }

    fun nextPhase() {
        when (currentPhase.value) {
            TimerPhase.PREPARATION -> {
                selectPhase(TimerPhase.WORKOUT, withoutStopping = true)
            }
            TimerPhase.WORKOUT -> {
                selectPhase(TimerPhase.REST, withoutStopping = true)
            }
            TimerPhase.REST -> {
                if (cyclesRemaining.value!! > 1) {
                    cyclesRemaining.value = cyclesRemaining.value!! - 1
                    selectPhase(TimerPhase.WORKOUT, withoutStopping = true)
                }
                else {
                    cyclesRemaining.value = 0
                    nextTimer(withoutStopping = true)
                }
            }
        }
    }

    fun selectPhase(phase: TimerPhase, withoutStopping: Boolean) {
        currentPhase.value = phase

        when (phase) {
            TimerPhase.PREPARATION -> {
                currentPhaseRemaining.value = currentTimer.value!!.preparations
            }
            TimerPhase.WORKOUT -> {
                currentPhaseRemaining.value = currentTimer.value!!.workout
            }
            TimerPhase.REST -> {
                currentPhaseRemaining.value = currentTimer.value!!.rest
            }
        }

        currentTimerHandler?.cancel()
        stop()
        if (withoutStopping) {
            start()
        }
    }

    fun selectTimer(timerPos: Int) {
        if (timerPos in 0 until timersCount) {
            stop()
            currentTimerPos.value = timerPos
            currentTimer.value = allUpcomingTimers.value!![timerPos]
            selectPhase(TimerPhase.PREPARATION, withoutStopping = false)
        }
    }

    inner class TimerEventCallback : TimerHandler.EventCallback {
        override fun onStart() {
            timerCallback?.onStart()
        }

        override fun onCancel() {}

        override fun onTick() {
            currentPhaseRemaining.value = currentPhaseRemaining.value!! - 1
        }

        override fun onFinish() {
            timerCallback?.onStop()
            nextPhase()
        }
    }
}