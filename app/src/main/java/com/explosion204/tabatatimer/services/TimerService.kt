package com.explosion204.tabatatimer.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.explosion204.tabatatimer.Constants.ACTION_TIMER_STATE_CHANGED
import com.explosion204.tabatatimer.Constants.MAIN_NOTIFICATION_CHANNEL
import com.explosion204.tabatatimer.Constants.NOTIFICATION_BROADCAST_ACTION
import com.explosion204.tabatatimer.Constants.NOTIFICATION_ID
import com.explosion204.tabatatimer.Constants.SEQUENCE_FINISHED
import com.explosion204.tabatatimer.Constants.TIMER_BROADCAST_ACTION
import com.explosion204.tabatatimer.Constants.TIMER_STARTED
import com.explosion204.tabatatimer.Constants.TIMER_ACTION_TYPE
import com.explosion204.tabatatimer.Constants.TIMER_STOPPED
import com.explosion204.tabatatimer.R
import com.explosion204.tabatatimer.data.entities.SequenceWithTimers
import com.explosion204.tabatatimer.data.entities.Timer

class TimerService : LifecycleService() {
    private lateinit var notificationReceiver: BroadcastReceiver
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var ringSound: MediaPlayer

    override fun onCreate() {
        super.onCreate()

        val filter = IntentFilter(NOTIFICATION_BROADCAST_ACTION)
        notificationReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.getStringExtra(ACTION_TIMER_STATE_CHANGED)) {
                    TIMER_STARTED -> start()
                    TIMER_STOPPED -> stop()
                }
            }
        }

        notificationManager = NotificationManagerCompat.from(this)
        registerReceiver(notificationReceiver, filter)
        ringSound = MediaPlayer.create(this, R.raw.ring_sound)
    }

    override fun onDestroy() {
        super.onDestroy()
        stop()
        unregisterReceiver(notificationReceiver)
        notificationManager.cancel(NOTIFICATION_ID)
    }

    private var currentTimerHandler: TimerHandler? = null
    private lateinit var allTimers: ArrayList<Timer>

    var timersCount = MutableLiveData(0)
    var currentTimerPos = MutableLiveData(0)
    var currentTimer = MutableLiveData<Timer>()
    var currentPhase = MutableLiveData(com.explosion204.tabatatimer.services.TimerPhase.PREPARATION)

    var preparationRemaining = MutableLiveData(0)
    var workoutRemaining = MutableLiveData(0)
    var restRemaining = MutableLiveData(0)
    var cyclesRemaining = MutableLiveData(0)

    private var currentPhaseRemaining = 0
    private var timerIsRunning = false

    fun setSequence(sequence: SequenceWithTimers) {
        allTimers = sequence.timers as ArrayList<Timer>
        initService()
    }

    fun setTimer(timer: Timer) {
        allTimers = arrayListOf(timer)
        initService()
    }

    private fun initService() {
        timersCount.value = allTimers.size
        currentTimerPos.value = 0
        currentTimer.value = allTimers[0]
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
        timerIsRunning = true
        notifyTimerStarted()
    }

    fun stop() {
        currentTimerHandler?.cancel()
        timerIsRunning = false
        showNotification()
        notifyTimerStopped()
    }

    fun nextTimer(withoutStopping: Boolean) {
        if (currentTimerPos.value!! + 1 < timersCount.value!!) {
            currentTimerPos.value = currentTimerPos.value!! + 1
            currentTimer.value = allTimers[currentTimerPos.value!!]
            cyclesRemaining.value = currentTimer.value!!.cycles
            selectPhase(TimerPhase.PREPARATION, withoutStopping)
            showNotification()
        }

        notifySequenceFinished()
        stop()
        notificationManager.cancel(NOTIFICATION_ID)
    }

    fun prevTimer(withoutStopping: Boolean) {
        if (currentTimerPos.value!! - 1 >= 0) {
            currentTimerPos.value = currentTimerPos.value!! - 1
            currentTimer.value = allTimers[currentTimerPos.value!!]
            cyclesRemaining.value = currentTimer.value!!.cycles
            selectPhase(TimerPhase.PREPARATION, withoutStopping)
            showNotification()
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
            currentTimer.value = allTimers[timerPos]
            selectPhase(TimerPhase.PREPARATION, withoutStopping = false)
            showNotification()
        }
    }

    private fun notifyTimerStarted() {
        val intent = Intent(TIMER_BROADCAST_ACTION)
        intent.putExtra(TIMER_ACTION_TYPE, TIMER_STARTED)
        sendBroadcast(intent)
    }

    private fun notifyTimerStopped() {
        val intent = Intent(TIMER_BROADCAST_ACTION)
        intent.putExtra(TIMER_ACTION_TYPE, TIMER_STOPPED)
        sendBroadcast(intent)
    }

    private fun notifySequenceFinished() {
        val intent = Intent(TIMER_BROADCAST_ACTION)
        intent.putExtra(TIMER_ACTION_TYPE, SEQUENCE_FINISHED)
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
            showNotification()
        }

        override fun onFinish() {
            nextPhase()
            ringSound.start()
        }
    }

    private fun showNotification() {
        val builder = NotificationCompat.Builder(this, MAIN_NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_timer_24)
            .setContentTitle("${currentPhaseRemaining / 60}m ${currentPhaseRemaining % 60}s")
            .setPriority(NotificationManager.IMPORTANCE_MAX)

        if (timerIsRunning) {
            val intent = Intent(NOTIFICATION_BROADCAST_ACTION)
            intent.putExtra(ACTION_TIMER_STATE_CHANGED, TIMER_STOPPED)
            val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            builder.addAction(R.drawable.ic_check_24, getString(R.string.stop), pendingIntent)
        }
        else {
            val intent = Intent(NOTIFICATION_BROADCAST_ACTION)
            intent.putExtra(ACTION_TIMER_STATE_CHANGED, TIMER_STARTED)
            val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            builder.addAction(R.drawable.ic_check_24, getString(R.string.start), pendingIntent)
        }

        var maxProgress = 0
        when (currentPhase.value) {
            TimerPhase.PREPARATION -> {
                maxProgress = currentTimer.value!!.preparations
                builder.setSubText(getString(R.string.preparation))
            }
            TimerPhase.WORKOUT -> {
                maxProgress = currentTimer.value!!.workout
                builder.setSubText(getString(R.string.workout))
            }
            TimerPhase.REST -> {
                maxProgress = currentTimer.value!!.rest
                builder.setSubText(getString(R.string.rest))
            }
            else -> builder
        }.setProgress(maxProgress, maxProgress - currentPhaseRemaining, false)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)

        return TimerServiceBinder()
    }

    inner class TimerServiceBinder : Binder() {
        fun getService() = this@TimerService
    }
}