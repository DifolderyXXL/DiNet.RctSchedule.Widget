package com.example.rctschedule.Di

import androidx.lifecycle.ViewModel
import com.example.rctschedule.Model.ScheduleUpdateConfig
import com.example.rctschedule.Data.TransformConfig
import com.example.rctschedule.Services.Time.NowTimeProvider
import com.example.rctschedule.Services.Time.TimeProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.LocalDate
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ScheduleModule {
    @Provides
    fun provideConfig() : ScheduleUpdateConfig
    {
        return ScheduleUpdateConfig(60)
    }

    @Provides
    fun provideTransformConfig() : TransformConfig
    {
        return TransformConfig(0)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object TimeProviderModule {
    @Provides
    fun provideTimeProvider() : TimeProvider
    {
        return NowTimeProvider()
        //return MockTimeProvider(LocalDate.of(2026, 3, 15))
    }
}
