package com.explosion204.tabatatimer.di.modules

import com.explosion204.tabatatimer.ui.list_fragments.SequenceListFragment
import com.explosion204.tabatatimer.ui.list_fragments.TimerListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuilderModule {
    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    abstract fun contributesInjectTimersListFragment(): TimerListFragment

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    abstract fun contributesInjectSequencesListFragment(): SequenceListFragment
}