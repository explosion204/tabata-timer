package com.explosion204.tabatatimer.services

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.explosion204.tabatatimer.Constants.TIMER_BROADCAST_ACTION
import com.explosion204.tabatatimer.Constants.TIMER_STARTED
import com.explosion204.tabatatimer.Constants.TIMER_STATE
import com.explosion204.tabatatimer.Constants.TIMER_STOPPED
import com.explosion204.tabatatimer.data.entities.SequenceWithTimers
import com.explosion204.tabatatimer.data.entities.Timer

class TimerService : LifecycleService() {
    private var currentTimerHandler: TimerHandler? = null
    private lateinit var sequence: SequenceWithTimers

    var timersCount = MutableLiveData(0)
    var currentTimerPos = MutableLiveData(0)
    var currentTimer = MutableLiveData<Timer>()
    var currentPhase = MutableLiveData(com.explosion204.tabatatimer.services.TimerPhase.PREPARATION)

    var preparationRemaining = MutableLiveData(0)
    var workoutRemaining = MutableLiveData(0)
    var restRemaining = MutableLiveData(0)
    var cyclesRemaining = MutableLiveData(0)

    private var currentPhaseRemaining = 0

    fun setSequence(sequence: SequenceWithTimers) {
        this.sequence = sequence
        timersCount.value = sequence.timers.size
        currentTimerPos.value = 0
        currentTimer.value = sequence.timers[0]
        preparationRemaining.value = currentTimer.value!!.preparations
        workoutRemaining.value = currentTimer.value!!.workout
        restRemaining.value = currentTimer.value!!.rest
        cyclesRemaining.value = currentTimer.value!!.cycles

        currentPhaseRemaining = currentTimer.value!!.preparations
    }

    fun start() {
        currentTimerHandler = TimerHandler(currentPhaseRemaining.toLong() * 1000 + 1000)
        currentTimerHandler!!.setEventCallback(TimerEventCallback())
        currentTimerHandler!!.start()
        notifyTimerStarted()
    }

    fun stop() {
        currentTimerHandler?.cancel()
        notifyTimerStopped()
    }

    fun nextTimer(withoutStopping: Boolean) {
        if (currentTimerPos.value!! + 1 < timersCount.value!!) {
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
                preparationRemaining.value = currentTimer.value!!.preparations
                currentPhaseRemaining = currentTimer.value!!.preparations
            }
            TimerPhase.WORKOUT -> {
                workoutRemaining.value = currentTimer.value!!.workout
                currentPhaseRemaining = currentTimer.value!!.workout
            }
            TimerPhase.REST -> {
                restRemaining.value = currentTimer.value!!.rest
                currentPhaseRemaining = currentTimer.value!!.rest
            }
        }

        currentTimerHandler?.cancel()
        if (!withoutStopping) {
            stop()
        }
        else {
            start()
        }
    }

    fun selectTimer(timerPos: Int) {
        if (timerPos in 0 until timersCount.value!!) {
            stop()
            currentTimerPos.value = timerPos
            currentTimer.value = sequence.timers[timerPos]
            selectPhase(TimerPhase.PREPARATION, withoutStopping = false)
        }
    }

    private fun notifyTimerStarted() {
        val intent = Intent(TIMER_BROADCAST_ACTION)
        intent.putExtra(TIMER_STATE, TIMER_STARTED)
        sendBroadcast(intent)
    }

    private fun notifyTimerStopped() {
        val intent = Intent(TIMER_BROADCAST_ACTION)
        intent.putExtra(TIMER_STATE, TIMER_STOPPED)
        sendBroadcast(intent)
    }

    inner class TimerEventCallback : TimerHandler.EventCallback {
        override fun onStart() {
            notifyTimerStarted()
        }

        override fun onCancel() {}

        override fun onTick() {
            when (currentPhase.value!!) {
                TimerPhase.PREPARATION -> {
                    preparationRemaining.value = preparationRemaining.value!! - 1
                }
                TimerPhase.WORKOUT -> {
                    workoutRemaining.value = workoutRemaining.value!! - 1
                }
                TimerPhase.REST -> {
                    restRemaining.value = restRemaining.value!! - 1
                }
            }

            currentPhaseRemaining--
        }

        override fun onFinish() {
            nextPhase()
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)

        return TimerServiceBinder()
    }

    inner class TimerServiceBinder : Binder() {
        fun getService() = this@TimerService
    }
}