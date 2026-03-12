package com.example.rctschedule.Model

import android.content.Context
import com.example.rctschedule.Services.Repositories.ApplicationSettingsRepository
import com.example.rctschedule.Services.Repositories.ScheduleDataRepository
import com.example.rctschedule.Services.Repositories.SelectedDayRepository
import com.example.rctschedule.UseCases.ChangeGroupUseCase
import com.example.rctschedule.UseCases.ScheduleNavigationUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

sealed interface DataState {

    object Null: DataState
    object Loading : DataState
    object Error : DataState
    data class Completed(val table: ScheduleWeekData, val updateTime: Date) : DataState
}

sealed interface FetchState
{
    object Null : FetchState
    object Fetching : FetchState
    object Error : FetchState
    object Completed: FetchState
}



@Singleton
class WidgetModelRepository  @Inject constructor(
){
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WidgetModelRepositoryEntrypoint {
        fun widgetModelRepository(): WidgetModelRepository
        fun selectedDayRepository(): SelectedDayRepository
        fun scheduleNavigationUseCase(): ScheduleNavigationUseCase
        fun applicationSettingsRepository(): ApplicationSettingsRepository
        fun getChangeGroupUseCase(): ChangeGroupUseCase
    }

    companion object {
        @Volatile
        private var cachedEntryPoint: WidgetModelRepositoryEntrypoint? = null

        fun get(applicationContext: Context): WidgetModelRepositoryEntrypoint {
            return cachedEntryPoint ?: synchronized(this){
                cachedEntryPoint ?: EntryPoints.get(
                    applicationContext,
                    WidgetModelRepositoryEntrypoint::class.java,
                ).also { cachedEntryPoint = it }
            }
        }
    }

}