package com.explosion204.tabatatimer.di.modules

import com.explosion204.tabatatimer.ui.fragments.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuilderModule {
    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    abstract fun contributesInjectTimerListFragment(): TimerListFragment

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    abstract fun contributesInjectSequenceListFragment(): SequenceListFragment

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    abstract fun contributesInjectSequenceDetailFragment(): SequenceDetailFragment

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    abstract fun contributesInjectSequenceTimerListFragment(): SequenceTimerListFragment
}