package com.explosion204.tabatatimer.viewmodels

import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {
    interface ActionCallback {
        fun callback(action: String, arg: Any?)
    }

    private var activityCallback: ActionCallback? = null
    private var fragmentCallback: ActionCallback? = null

    fun setActivityCallback(callback: ActionCallback) {
        activityCallback = callback
    }

    fun setFragmentCallback(callback: ActionCallback) {
        fragmentCallback = callback
    }

    fun sendActionToActivity(action: String, arg: Any?) {
        if (activityCallback != null) {
            activityCallback!!.callback(action, arg)
        }
    }

    fun sendActionToFragment(action: String, arg: Any?) {
        if (fragmentCallback != null) {
            fragmentCallback!!.callback(action, arg)
        }
    }
}