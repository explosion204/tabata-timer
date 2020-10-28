package com.explosion204.tabatatimer.di.modules

import androidx.lifecycle.ViewModel
import com.explosion204.tabatatimer.di.annotations.ViewModelKey
import com.explosion204.tabatatimer.viewmodels.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(TimerListViewModel::class)
    abstract fun bindTimerListViewModel(timerViewModel: TimerListViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SequenceListViewModel::class)
    abstract fun bindSequenceListViewModel(sequenceListViewModel: SequenceListViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TimerDetailViewModel::class)
    abstract fun bindTimerDetailViewModel(timerViewModel: TimerDetailViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SequenceDetailViewModel::class)
    abstract fun bindSequenceViewModel(sequenceDetailViewModel: SequenceDetailViewModel) : ViewModel
}