package com.example.rctschedule.Modules

import com.example.rctschedule.Model.ScheduleUpdateConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object ScheduleModule {
    @Provides
    fun provideConfig() : ScheduleUpdateConfig
    {
        return ScheduleUpdateConfig(60)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule{
    @IoDispatcher
    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}