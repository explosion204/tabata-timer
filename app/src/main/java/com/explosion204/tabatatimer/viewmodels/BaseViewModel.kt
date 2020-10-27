package com.explosion204.tabatatimer.viewmodels

import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {
    interface ActionCallback {
        fun callback(action: String, arg: Any?)
    }

    private val fragmentCallbacks = mutableMapOf<String, ActionCallback>()

    private var activityCallback: ActionCallback? = null

    fun setActivityCallback(callback: ActionCallback) {
        activityCallback = callback
    }

    fun setFragmentCallback(fragmentTag: String, callback: ActionCallback) {
        fragmentCallbacks[fragmentTag] = callback
    }

    fun sendActionToActivity(action: String, arg: Any?) {
        if (activityCallback != null) {
            activityCallback!!.callback(action, arg)
        }
    }

    fun sendActionToFragment(fragmentTag: String, action: String, arg: Any?) {
        fragmentCallbacks[fragmentTag]!!.callback(action, arg)
    }
}