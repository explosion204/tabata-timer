package com.explosion204.tabatatimer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.explosion204.tabatatimer.data.dao.BaseDao
import com.explosion204.tabatatimer.data.entities.BaseEntity
import com.explosion204.tabatatimer.data.repos.BaseRepository
import com.explosion204.tabatatimer.data.repos.SequenceRepository
import kotlinx.coroutines.launch

open class ListViewModel<T : BaseEntity, D : BaseDao<T>>(private val repository: BaseRepository<T, D>) : ViewModel() {
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
}