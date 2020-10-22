package com.explosion204.tabatatimer.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.explosion204.tabatatimer.data.dao.BaseDao
import com.explosion204.tabatatimer.data.entities.BaseEntity
import com.explosion204.tabatatimer.data.repos.BaseRepository
import kotlinx.coroutines.launch

open class ListViewModel<T : BaseEntity, D : BaseDao<T>>(private val repository: BaseRepository<T, D>) : ViewModel() {
    interface ActionCallback {
        fun callback(action: String, arg: Any)
    }

    private var activityCallback: ActionCallback? = null
    private var fragmentCallback: ActionCallback? = null

    fun setActivityCallback(callback: ActionCallback) {
        activityCallback = callback
    }

    fun setFragmentCallback(callback: ActionCallback) {
        fragmentCallback = callback
    }

    fun sendActionToActivity(action: String, arg: Any) {
        if (activityCallback != null) {
            activityCallback!!.callback(action, arg)
        }
    }

    fun sendActionToFragment(action: String, arg: Any) {
        if (fragmentCallback != null) {
            fragmentCallback!!.callback(action, arg)
        }
    }

    fun insert(item: T) {
        viewModelScope.launch {
            repository.insert(item)
        }
    }

    fun update(item: T) {
        viewModelScope.launch {
            repository.update(item)
        }
    }

    fun delete(item: T) {
        viewModelScope.launch {
            repository.delete(item)
        }
    }

    fun delete(items: ArrayList<T>) {
        viewModelScope.launch {
            for (item in items) {
                repository.delete(item)
            }
        }
    }
}