package com.explosion204.tabatatimer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.explosion204.tabatatimer.Constants.MAIN_NOTIFICATION_CHANNEL
import com.explosion204.tabatatimer.di.components.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class TabataTimerApp : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().application(this).build()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                MAIN_NOTIFICATION_CHANNEL,
                "Main Notification Channel",
                NotificationManager.IMPORTANCE_HIGH
            )

            channel.description = "${getString(R.string.app_name)} App Notification Channel"

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}