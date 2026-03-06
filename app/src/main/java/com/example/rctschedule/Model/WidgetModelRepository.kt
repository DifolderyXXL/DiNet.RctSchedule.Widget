package com.example.rctschedule.Model

import android.content.Context
import com.example.rctschedule.Services.ApplicationSettingsRepository
import com.example.rctschedule.Services.ScheduleDataRepository
import com.example.rctschedule.ViewModels.WidgetViewModel
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
    val repository: ScheduleDataRepository,
    val appSettings: ApplicationSettingsRepository
){
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WidgetModelRepositoryEntrypoint {
        fun widgetModelRepository(): WidgetModelRepository
    }

    companion object {
        fun get(applicationContext: Context): WidgetModelRepository {
            var widgetModelRepositoryEntryoint: WidgetModelRepositoryEntrypoint = EntryPoints.get(
                applicationContext,
                WidgetModelRepositoryEntrypoint::class.java,
            )
            return widgetModelRepositoryEntryoint.widgetModelRepository()
        }
    }

    public fun loadOrCreate() : WidgetViewModel
    {
        return WidgetViewModel(repository, appSettings)
    }

}