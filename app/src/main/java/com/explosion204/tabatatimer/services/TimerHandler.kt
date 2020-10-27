package com.explosion204.tabatatimer.services

import android.os.CountDownTimer

class TimerHandler(private val length: Long) {
    interface EventCallback {
        fun onStart()
        fun onCancel()
        fun onTick()
        fun onFinish()
    }

    private var eventCallback: EventCallback? = null

    fun setEventCallback(eventCallback: EventCallback) {
        this.eventCallback = eventCallback
    }

    private var countDownTimer: CountDownTimer = object : CountDownTimer(length, 1000) {
        override fun onFinish() {
            eventCallback?.onFinish()
        }

        override fun onTick(millisUntilFinished: Long) {
            eventCallback?.onTick()
        }
    }

    fun start() {
        countDownTimer.start()
        eventCallback?.onStart()
    }

    fun cancel() {
        countDownTimer.cancel()
        eventCallback?.onCancel()
    }
}