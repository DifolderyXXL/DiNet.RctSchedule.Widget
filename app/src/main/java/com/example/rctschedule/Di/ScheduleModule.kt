package com.example.rctschedule.Di

import androidx.lifecycle.ViewModel
import com.example.rctschedule.Model.ScheduleUpdateConfig
import com.example.rctschedule.Data.TransformConfig
import com.example.rctschedule.Model.WidgetModelRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
