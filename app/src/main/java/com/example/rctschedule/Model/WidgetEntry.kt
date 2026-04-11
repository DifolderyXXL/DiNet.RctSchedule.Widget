package com.example.rctschedule.Model

import android.content.Context
import com.example.rctschedule.Services.Parsing.ISheetRegularContextProvider
import com.example.rctschedule.Services.Repositories.ApplicationSettingsRepository
import com.example.rctschedule.Services.Repositories.GroupToggleRepository
import com.example.rctschedule.Services.Repositories.ScheduleDataRepository
import com.example.rctschedule.UseCases.ChangeCourseUseCase
import com.example.rctschedule.UseCases.ChangeGroupUseCase
import com.example.rctschedule.UseCases.GetAppSettingsUseCase
import com.example.rctschedule.UseCases.SelectDisplayUseCase
import com.example.rctschedule.ViewModels.DaySelectionPresenter
import com.example.rctschedule.ViewModels.WeekSelectionPresenter
import com.example.rctschedule.Views.ScheduleWidgetLoader
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {

    fun getScheduleDataRepository(): ScheduleDataRepository
    fun getAppSettingsRepository(): ApplicationSettingsRepository
    fun getChangeGroupUseCase(): ChangeGroupUseCase
    fun getChangeCourseUseCase(): ChangeCourseUseCase
    fun getSheetRegularContextProvider(): ISheetRegularContextProvider
    fun getGetAppSettingsUseCase(): GetAppSettingsUseCase

    fun getGroupToggleRepository(): GroupToggleRepository
    fun getDaySelectionPresenter(): DaySelectionPresenter
    fun getWeekSelectionPresenter(): WeekSelectionPresenter

    fun getScheduleWidgetLoader(): ScheduleWidgetLoader

    fun getSelectDisplayUseCase() : SelectDisplayUseCase
}

@Singleton
class WidgetEntry  @Inject constructor(
){
    companion object {
        @Volatile
        private var cachedEntryPoint: WidgetEntryPoint? = null

        fun get(applicationContext: Context): WidgetEntryPoint {
            return cachedEntryPoint ?: synchronized(this){
                cachedEntryPoint ?: EntryPoints.get(
                    applicationContext,
                    WidgetEntryPoint::class.java,
                ).also { cachedEntryPoint = it }
            }
        }
    }
}