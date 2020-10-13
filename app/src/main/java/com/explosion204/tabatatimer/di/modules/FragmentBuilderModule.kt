package com.explosion204.tabatatimer.di.modules

import com.explosion204.tabatatimer.MainActivity
import com.explosion204.tabatatimer.ui.list_fragments.TimersListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {
    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    abstract fun contributesInjectTimersListFragment(): TimersListFragment
}