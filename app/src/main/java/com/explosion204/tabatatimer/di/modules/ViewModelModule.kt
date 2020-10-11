package com.explosion204.tabatatimer.di.modules

import androidx.lifecycle.ViewModel
import com.explosion204.tabatatimer.di.annotations.ViewModelKey
import com.explosion204.tabatatimer.viewmodels.TimerListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(TimerListViewModel::class)
    abstract fun bindMainViewModel(timerViewModel: TimerListViewModel) : ViewModel
}