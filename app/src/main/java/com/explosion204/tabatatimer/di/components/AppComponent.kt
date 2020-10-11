package com.explosion204.tabatatimer.di.components

import android.app.Application
import com.explosion204.tabatatimer.TabataTimerApp
import com.explosion204.tabatatimer.di.modules.ActivityBuilderModule
import com.explosion204.tabatatimer.di.modules.DatabaseModule
import com.explosion204.tabatatimer.di.modules.ViewModelFactoryModule
import com.explosion204.tabatatimer.di.modules.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AndroidSupportInjectionModule::class,
        ActivityBuilderModule::class,
        DatabaseModule::class,
        ViewModelFactoryModule::class,
        ViewModelModule::class
    ]
)
interface AppComponent : AndroidInjector<TabataTimerApp> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application) : Builder
        fun build(): AppComponent
    }

}