package com.example.rctschedule.Activities.di

import android.content.Context
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.rctschedule.Activities.pages.aboutContentEntryBuilder
import com.example.rctschedule.Activities.pages.debugContentEntryBuilder
import com.example.rctschedule.Activities.pages.homeContentEntryBuilder
import com.example.rctschedule.Workers.WorkerScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object NavigationModule {
    @IntoSet
    @Provides
    fun provideNavigation(
        scheduler: WorkerScheduler,
        @ApplicationContext context: Context
    ) : EntryProviderScope<NavKey>.() -> Unit = {
        debugContentEntryBuilder(scheduler, context)
        homeContentEntryBuilder()
        aboutContentEntryBuilder()
    }
}

