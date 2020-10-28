package com.explosion204.tabatatimer.di.modules

import com.explosion204.tabatatimer.MainActivity
import com.explosion204.tabatatimer.ui.activities.SequenceDetailActivity
import com.explosion204.tabatatimer.ui.activities.TimerActivity
import com.explosion204.tabatatimer.ui.activities.TimerDetailActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {
    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    abstract fun contributesInjectMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    abstract fun contributesInjectTimerDetailActivity(): TimerDetailActivity

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    abstract fun contributesInjectSequenceDetailActivity(): SequenceDetailActivity
}