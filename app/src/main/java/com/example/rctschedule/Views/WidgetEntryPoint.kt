package com.example.rctschedule.Views

import com.example.rctschedule.Services.Repositories.ApplicationSettingsRepository
import com.example.rctschedule.Services.Repositories.GroupToggleRepository
import com.example.rctschedule.Services.Repositories.ScheduleDataRepository
import com.example.rctschedule.ViewModels.DaySelectionPresenter
import com.example.rctschedule.ViewModels.WeekSelectionPresenter
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {

    fun getScheduleDataRepository(): ScheduleDataRepository
    fun getAppSettingsRepository(): ApplicationSettingsRepository
    fun getGroupToggleRepository(): GroupToggleRepository
    fun getDaySelectionPresenter(): DaySelectionPresenter
    fun getWeekSelectionPresenter(): WeekSelectionPresenter

    fun getScheduleWidgetLoader(): ScheduleWidgetLoader
}