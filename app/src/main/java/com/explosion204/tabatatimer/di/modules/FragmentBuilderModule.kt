package com.explosion204.tabatatimer.di.modules

import com.explosion204.tabatatimer.ui.fragments.SequenceDetailFragment
import com.explosion204.tabatatimer.ui.fragments.SequenceListFragment
import com.explosion204.tabatatimer.ui.fragments.SequenceTimerListFragment
import com.explosion204.tabatatimer.ui.fragments.TimerListFragment
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